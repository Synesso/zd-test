package zdtest.repo

import java.io.File

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import zdtest.TestRepository
import zdtest.domain.{ArbitraryInput, Organisation, Ticket, User}

import scala.concurrent.duration._

class RepositorySpec(implicit ee: ExecutionEnv) extends Specification with ArbitraryInput {

  "a repository" should {
    "be able to be empty" >> {
      Repository() must not(throwAn[Exception])
    }

    "map organisations" >> {
      val orgs = (0 to 100).flatMap(_ => genOrg.sample)
      val withDistinctIds = orgs.zipWithIndex.map { case (org, i) => org.copy(_id = i) }
      val actual = Repository(orgList = withDistinctIds).organisations
      actual.values.toSeq.sortBy(_._id) mustEqual withDistinctIds
      forall(withDistinctIds) { o: Organisation => actual.get(o._id) must beSome(o) }
    }

    "map users" >> {
      val users = (0 to 100).flatMap(_ => genUser.sample)
      val withDistinctIds = users.zipWithIndex.map { case (user, i) => user.copy(_id = i) }
      val actual = Repository(userList = withDistinctIds).users
      actual.values.toSeq.sortBy(_._id) mustEqual withDistinctIds
      forall(withDistinctIds) { u: User => actual.get(u._id) must beSome(u) }
    }

    "map tickets" >> {
      val tickets = (0 to 100).flatMap(_ => genTicket.sample)
      val withDistinctIds = tickets.map(t => t._id -> t).toMap.values.toSeq
      val actual = Repository(ticketList = withDistinctIds).tickets
      actual.values must containTheSameElementsAs(withDistinctIds)
      forall(withDistinctIds) { t: Ticket => actual.get(t._id) must beSome(t) }
    }

  }

  "instantiating a repository from files" should {
    "build correctly with sample data" >> {
      TestRepository.repo must beLike[Repository] { case repo =>
        repo.organisations.values must
          containTheSameElementsAs(Parser.parseOrgs(new File("src/test/resources/organizations.json")))
        repo.users.values must
          containTheSameElementsAs(Parser.parseUsers(new File("src/test/resources/users.json")))
        repo.tickets.values must
          containTheSameElementsAs(Parser.parseTickets(new File("src/test/resources/tickets.json")))
      }
    }

    "fail if the dir is invalid" >> {
      Repository.fromDir(new File("/foo")) must throwAn[IllegalArgumentException].await
    }

    "fail if the files are not present" >> {
      Repository.fromDir(new File(".")) must throwAn[IllegalArgumentException].await
    }
  }

}
