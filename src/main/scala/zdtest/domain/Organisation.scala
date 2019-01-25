package zdtest.domain

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

case class Organisation(_id: Long,
                        url: String,
                        external_id: String,
                        name: String,
                        domain_names: Seq[String],
                        created_at: OffsetDateTime,
                        details: String,
                        shared_tickets: Boolean,
                        tags: Seq[String])

object Organisation extends CommonRW {
  import upickle.default._

  implicit val rw: ReadWriter[Organisation] = macroRW[Organisation]
}

