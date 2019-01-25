package zdtest.domain

import org.specs2.mutable.Specification

class RepositorySpec extends Specification with ArbitraryInput {

  "a repository" should {
    val repo = Repository()

    "add new organisations" >> prop { orgs: Seq[Organisation] =>
      val withDistinctIds = orgs.zipWithIndex.map{ case (org, i) => org.copy(_id = i) }
      val actual = repo.withOrganisations(withDistinctIds).organisations
      actual.values.toSeq.sortBy(_._id) mustEqual withDistinctIds
      forall(withDistinctIds) { o: Organisation => actual.get(o._id) must beSome(o) }
    }
  }

}
