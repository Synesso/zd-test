package zdtest.domain

import org.specs2.mutable.Specification
import upickle.default._

class UserSpec extends Specification with ArbitraryInput {

  "a user" should {
    "serde via json" >> prop { user: User =>
      val json = write[User](user)
      read[User](json) mustEqual user
    }
  }

}
