package zdtest.repo

import java.io.File

import org.scalacheck.Gen
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import zdtest.domain.{ArbitraryInput, Organisation, User}

class RepositorySpec(implicit ee: ExecutionEnv) extends Specification with ArbitraryInput {

  "a repository" should {
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
  }

  "instantiating a repository from files" should {
    "build correctly with sample data" >> {
      Repository.fromDir(new File("src/test/resources")) must beLike[Repository] { case repo =>
          repo.organisations.values must
            containTheSameElementsAs(Parser.parseOrgs(new File("src/test/resources/organizations.json")))
      }.await
    }
  }

}
