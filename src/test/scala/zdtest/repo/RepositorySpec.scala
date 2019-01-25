package zdtest.repo

import java.io.File

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import zdtest.domain.{ArbitraryInput, Organisation}

class RepositorySpec(implicit ee: ExecutionEnv) extends Specification with ArbitraryInput {

  "a repository" should {
    val repo = Repository()

    "add new organisations" >> prop { orgs: Seq[Organisation] =>
      val withDistinctIds = orgs.zipWithIndex.map{ case (org, i) => org.copy(_id = i) }
      val actual = repo.withOrganisations(withDistinctIds).organisations
      actual.values.toSeq.sortBy(_._id) mustEqual withDistinctIds
      forall(withDistinctIds) { o: Organisation => actual.get(o._id) must beSome(o) }
    }
  }

  "instantiating a repository from files" should {
    "build correctly with sample data" >> {
      Repository(new File("src/test/resources")) must beLike[Repository] { case repo =>
          repo.organisations.values must
            containTheSameElementsAs(Parser.parseOrgs(new File("src/test/resources/organizations.json")))
      }.await
    }
  }

}
