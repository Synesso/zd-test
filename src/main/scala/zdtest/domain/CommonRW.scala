package zdtest.domain

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import upickle.default._

trait CommonRW {
  implicit val dateTimeRW: ReadWriter[OffsetDateTime] = {
    readwriter[String].bimap[OffsetDateTime](
      DateFormat.formatter.format(_),
      OffsetDateTime.parse(_, DateFormat.formatter)
    )
  }
}
