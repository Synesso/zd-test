package zdtest.domain

import java.time.OffsetDateTime

import upickle.default.{macroRW, ReadWriter => RW}

case class Ticket(_id: String,
                  created_at: OffsetDateTime,
                  due_at: OffsetDateTime,
                  url: String = "",
                  external_id: String = "",
                  `type`: String = "",
                  subject: String = "",
                  description: String = "",
                  priority: String = "",
                  status: String = "",
                  submitter_id: Long = -1,
                  assignee_id: Long = -1,
                  organization_id: Long = -1,
                  tags: Set[String] = Set.empty,
                  has_incidents: Boolean = false,
                  via: String = "")

object Ticket extends CommonRW {
  implicit val rw: RW[Ticket] = macroRW
}