package zdtest.domain

import org.specs2.mutable.Specification
import upickle.default._

class TicketSpec extends Specification with ArbitraryInput {

  "a ticket" should {
    "serde via json" >> prop { ticket: Ticket =>
      val json = write[Ticket](ticket)
      read[Ticket](json) mustEqual ticket
    }
  }

}
