package zdtest.domain

import org.specs2.mutable.Specification
import upickle.default._

class OrganisationSpec extends Specification with ArbitraryInput {

  "an organisation" should {
    "serde via json" >> prop { org: Organisation =>
      val json = write[Organisation](org)
      read[Organisation](json) mustEqual org
    }
  }

}
