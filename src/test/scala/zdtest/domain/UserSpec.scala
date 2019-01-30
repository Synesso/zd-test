package zdtest.domain

import org.specs2.mutable.Specification
import upickle.default._
import zdtest.TestRepository
import zdtest.TestRepository._

class UserSpec extends Specification with ArbitraryInput {

  "a user" should {
    "serde via json" >> prop { user: User =>
      val json = write[User](user)
      read[User](json) mustEqual user
    }

    "have a short description" >> {
      val desc = TestRepository.repo.users(37).shortDescription
      desc mustEqual "Denise Finch (Miss Bailey) - user #37"
    }

    "have a full description that includes organisation" >> {
      val desc = TestRepository.repo.users(37).fullDescription(repo, index)
      desc must contain("organization:    Kindaloo - #110")
    }

    "have a full description that indicates when the organisation can't be found" >> {
      val desc = TestRepository.repo.users(37).copy(organization_id = 999).fullDescription(repo, index)
      desc must contain("organization:    [not found] - #999")
    }

    "have a full description that includes associated tickets" >> {
      val desc = TestRepository.repo.users(37).fullDescription(repo, index)
      desc must contain("""related tickets: ticket 4b88dee7-0c17-4fe2-8cb6-914b7ce93dc3 - "A Drama in East Timor"
                          |                 ticket 5c66cef0-7abc-46df-b487-5f8eb6208422 - "A Problem in Switzerland"
                          |                 ticket b539a7db-1166-4537-9a5e-d2a97dd432bd - "A Catastrophe in Lesotho"
                          |                 ticket cb7cae87-2915-44d4-bda4-4ccb59c63bd4 - "A Drama in S. Georgia and S. Sandwich Isls."
                          |""".stripMargin)
    }
  }

}
