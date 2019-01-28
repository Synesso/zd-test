package zdtest.cli

import org.scalacheck.Gen
import org.specs2.mutable.Specification
import zdtest.domain._

class CategorySpec extends Specification with ArbitraryInput {

  "category by name" should {
    "parse name 'user'" >> {
      Category.withName("user") must beSome[Category](UserCat)
    }
    "parse name 'organisation'" >> {
      Category.withName("organisation") must beSome[Category](OrgCat)
    }
    "parse name 'organization'" >> {
      Category.withName("organization") must beSome[Category](OrgCat)
    }
    "parse name 'org'" >> {
      Category.withName("org") must beSome[Category](OrgCat)
    }
    "parse name 'ticket'" >> {
      Category.withName("ticket") must beSome[Category](TicketCat)
    }
    "not parse anything else" >> prop { s: String =>
      Category.withName(s) must beNone
    }.setGen(Gen.identifier.suchThat(s =>
      !Set("user", "organisation", "organization", "org", "ticket").contains(s)
    ))
  }
}
