package zdtest.domain

import java.time.OffsetDateTime

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import upickle.default._
import zdtest.TestRepository._

class OrganisationSpec(ee: ExecutionEnv) extends Specification with ArbitraryInput {

  "an organisation" should {
    "serde via json" >> prop { org: Organisation =>
      val json = write[Organisation](org)
      read[Organisation](json) mustEqual org
    }

    "have a short description" >> {
      Organisation(_id = 66, created_at = OffsetDateTime.now).shortDescription mustEqual s"[no name] - #66"
      Organisation(_id = 77, created_at = OffsetDateTime.now, name = "foo").shortDescription mustEqual s"foo - #77"
    }

    "have a full description that includes associated users" >> {
      val org = repo.organisations(105)
      val desc = org.fullDescription(repo, index)
      desc must contain("""related users:   Kari Vinson (Mr Webb) - user #10
                          |                 Lee Dotson (Mr Eve) - user #32""".stripMargin)
    }

    "ensure that associated users are a full id match, not a partial match" >> {
      val org = repo.organisations(101).copy(_id = 1)
      // will now look for users associated with org #1, which matches all users with an org id.
      // test for a full id match, and expect zero users.
      val desc = org.fullDescription(repo, index)
      desc must contain("related users:   none")
    }
  }

}
