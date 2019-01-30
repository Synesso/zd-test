package zdtest.domain

import org.specs2.mutable.Specification
import upickle.default._
import zdtest.TestRepository
import zdtest.TestRepository._

class TicketSpec extends Specification with ArbitraryInput {

  "a ticket" should {
    "serde via json" >> prop { ticket: Ticket =>
      val json = write[Ticket](ticket)
      read[Ticket](json) mustEqual ticket
    }

    "have a short description" >> {
      val desc = TestRepository.repo.tickets("1a227508-9f39-427c-8f57-1b72f3fab87c").shortDescription
      desc mustEqual """ticket 1a227508-9f39-427c-8f57-1b72f3fab87c - "A Catastrophe in Micronesia""""
    }

    "have a full description that includes submitter" >> {
      val desc = TestRepository.repo.tickets("1a227508-9f39-427c-8f57-1b72f3fab87c").fullDescription(repo, index)
      desc must contain("submitter:       Prince Hinton (Miss Dana) - user #71")
    }

    "have a full description that indicates when submitter can't be found" >> {
      val desc = TestRepository.repo.tickets("1a227508-9f39-427c-8f57-1b72f3fab87c")
        .copy(submitter_id = 999).fullDescription(repo, index)
      desc must contain("submitter:       [not found] - #999")
    }

    "have a full description that includes assignee" >> {
      val desc = TestRepository.repo.tickets("1a227508-9f39-427c-8f57-1b72f3fab87c").fullDescription(repo, index)
      desc must contain("assignee:        Elma Castro (Mr Georgette) - user #38")
    }

    "have a full description that indicates when assignee can't be found" >> {
      val desc = TestRepository.repo.tickets("1a227508-9f39-427c-8f57-1b72f3fab87c")
        .copy(assignee_id = 999).fullDescription(repo, index)
      desc must contain("assignee:        [not found] - #999")
    }

    "have a full description that includes organisation" >> {
      val desc = TestRepository.repo.tickets("1a227508-9f39-427c-8f57-1b72f3fab87c").fullDescription(repo, index)
      desc must contain("organization:    Quilk - #112")
    }

    "have a full description that indicates when assignee can't be found" >> {
      val desc = TestRepository.repo.tickets("1a227508-9f39-427c-8f57-1b72f3fab87c")
        .copy(organization_id = 999).fullDescription(repo, index)
      desc must contain("organization:    [not found] - #999")
    }
  }

}
