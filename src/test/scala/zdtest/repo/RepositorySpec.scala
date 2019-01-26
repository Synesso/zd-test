package zdtest.repo

import java.io.File

import org.scalacheck.Gen
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import zdtest.domain.{ArbitraryInput, Organisation, Ticket, User}

class RepositorySpec(implicit ee: ExecutionEnv) extends Specification with ArbitraryInput {

  "a repository" should {
    "be able to be empty" >> {
      Repository() must not(throwAn[Exception])
    }

    "map organisations" >> prop { orgs: Seq[Organisation] =>
      val withDistinctIds = orgs.zipWithIndex.map{ case (org, i) => org.copy(_id = i) }
      val actual = Repository(withDistinctIds).organisations
      actual.values.toSeq.sortBy(_._id) mustEqual withDistinctIds
      forall(withDistinctIds) { o: Organisation => actual.get(o._id) must beSome(o) }
    }.set(minTestsOk = 10)

    "map users" >> prop { (orgs: Seq[Organisation], users: Seq[User]) =>
      val usersMappedToOrgs = users.zip(Stream.continually(orgs).flatten).zipWithIndex.map {
        case ((user: User, org: Organisation), id: Int) => user.copy(_id = id, organization_id = org._id)
      }

      val actual = Repository(orgs, usersMappedToOrgs).users
      actual.values.toSeq.sortBy(_._id) mustEqual usersMappedToOrgs
      forall(usersMappedToOrgs) { u: User => actual.get(u._id) must beSome(u) }
    }.setGen1(Gen.nonEmptyListOf(genOrg)).set(minTestsOk = 10)

    "fail to map when user is linked to non-existent organisation" >> prop { (u: User, o: Organisation) =>
      val user = if (u.organization_id == o._id) u.copy(organization_id = u.organization_id + 1) else u
      Repository(Seq(o), Seq(user)) must throwAn[IllegalArgumentException]
    }.setGen1(genUser.suchThat(_.organization_id != -1))

    "map tickets" >> prop { (orgs: Seq[Organisation], users: Seq[User], tickets: Seq[Ticket]) =>
      val usersMappedToOrgs = users.zip(Stream.continually(orgs).flatten).zipWithIndex.map {
        case ((user: User, org: Organisation), id: Int) => user.copy(_id = id, organization_id = org._id)
      }
      val ticketsMappedToOrgsAndUsers = tickets.sortBy(_._id)
        .zip(Stream.continually(orgs).flatten)
        .zip(Stream.continually(usersMappedToOrgs).flatten).map {
        case ((ticket: Ticket, org: Organisation), user: User) =>
          ticket.copy(
            submitter_id = user._id,
            assignee_id = user._id,
            organization_id = org._id)
      }

      val actual = Repository(orgs, usersMappedToOrgs, ticketsMappedToOrgsAndUsers).tickets
      actual.values.toSeq.sortBy(_._id) mustEqual ticketsMappedToOrgsAndUsers
      forall(ticketsMappedToOrgsAndUsers) { t: Ticket => actual.get(t._id) must beSome(t) }

    }.setGen1(Gen.nonEmptyListOf(genOrg))
      .setGen2(Gen.nonEmptyListOf(genUser))
      .set(minTestsOk = 10)

    "fail to map ticket when assignee is linked to non-existent user" >> prop { (t: Ticket, u: User, o: Organisation) =>
      val user = u.copy(organization_id = o._id)
      val ticket = t.copy(submitter_id = user._id, organization_id = o._id, assignee_id = user._id + 1)
      Repository(Seq(o), Seq(user), Seq(ticket)) must throwAn[IllegalArgumentException]
    }

    "fail to map ticket when submitter is linked to non-existent user" >> prop { (t: Ticket, u: User, o: Organisation) =>
      val user = u.copy(organization_id = o._id)
      val ticket = t.copy(submitter_id = user._id + 1, organization_id = o._id, assignee_id = user._id)
      Repository(Seq(o), Seq(user), Seq(ticket)) must throwAn[IllegalArgumentException]
    }

    "fail to map ticket when org is linked to non-existent organisation" >> prop { (t: Ticket, u: User, o: Organisation) =>
      val user = u.copy(organization_id = o._id)
      val ticket = t.copy(submitter_id = user._id, organization_id = o._id + 1, assignee_id = user._id)
      Repository(Seq(o), Seq(user), Seq(ticket)) must throwAn[IllegalArgumentException]
    }
  }

  "instantiating a repository from files" should {
    "build correctly with sample data" >> {
      Repository.fromDir(new File("src/test/resources")) must beLike[Repository] { case repo =>
          repo.organisations.values must
            containTheSameElementsAs(Parser.parseOrgs(new File("src/test/resources/organizations.json")))
          repo.users.values must
            containTheSameElementsAs(Parser.parseUsers(new File("src/test/resources/users.json")))
      }.await
    }
  }

}
