package zdtest.search

import org.scalacheck.Gen
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import zdtest.domain._

import scala.concurrent.Await
import scala.concurrent.duration._

class IndexSpec(implicit ee: ExecutionEnv) extends Specification with ArbitraryInput {

  "building an index" should {
    "be successful when empty" >> {
      val index = Await.result(Index.build(), 5.seconds)
      index.search(UserCat, "_id", "1") mustEqual Nil
    }

    "allow searching on all fields on organisations" >> {
      val orgs = Gen.nonEmptyListOf(genOrg).sample.get
      val index = Await.result(Index.build(organisations = orgs), 5.seconds)
      val variants = for {
        org <- orgs
        (key, value) <- OrgCat.fields.mapValues(_(org))
        prefix = value.take(2)
      } yield (org, key, prefix)
      forall(variants) { case (org, key, prefix) =>
        println(key)
        index.search(OrgCat, key, prefix) must contain[Searchable](org)
      }
    }.pendingUntilFixed
  }

}
