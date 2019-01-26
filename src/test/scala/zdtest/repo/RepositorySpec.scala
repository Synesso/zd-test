package zdtest.repo

import java.io.File

import org.scalacheck.Gen
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import scala.concurrent.duration._
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
  }

  "instantiating a repository from files" should {
    "build correctly with sample data" >> {
      Repository.fromDir(new File("src/test/resources")) must beLike[Repository] { case repo =>
          repo.organisations.values must
            containTheSameElementsAs(Parser.parseOrgs(new File("src/test/resources/organizations.json")))
          repo.users.values must
            containTheSameElementsAs(Parser.parseUsers(new File("src/test/resources/users.json")))
          repo.tickets.values must
            containTheSameElementsAs(Parser.parseTickets(new File("src/test/resources/tickets.json")))
      }.awaitFor(5.seconds)
    }

    "fail if the dir is invalid" >> {
      Repository.fromDir(new File("/foo")) must throwAn[IllegalArgumentException].await
    }

    "fail if the files are not present" >> {
      Repository.fromDir(new File(".")) must throwAn[IllegalArgumentException].await
    }
  }

}
