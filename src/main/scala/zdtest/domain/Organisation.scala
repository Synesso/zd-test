package zdtest.domain

import java.time.OffsetDateTime

case class Organisation(_id: Long,
                        created_at: OffsetDateTime,
                        url: String = "",
                        external_id: String = "",
                        name: String = "",
                        domain_names: Seq[String] = Nil,
                        details: String = "",
                        shared_tickets: Boolean = false,
                        tags: Set[String] = Set.empty) extends Searchable

object Organisation extends CommonRW {
  import upickle.default._

  implicit val rw: ReadWriter[Organisation] = macroRW[Organisation]
}

