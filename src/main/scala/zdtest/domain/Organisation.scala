package zdtest.domain

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

case class Organisation(_id: Long,
                        url: String,
                        external_id: String,
                        name: String,
                        domain_names: Seq[String],
                        created_at: ZonedDateTime,
                        details: String,
                        shared_tickets: Boolean,
                        tags: Seq[String])

object Organisation {
  import upickle.default._

  implicit val dateTimeRW: ReadWriter[ZonedDateTime] = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss z")
    readwriter[String].bimap[ZonedDateTime](
      formatter.format(_),
      ZonedDateTime.parse(_, formatter)
    )
  }
  implicit val rw: ReadWriter[Organisation] = macroRW[Organisation]

}

