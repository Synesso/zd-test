package zdtest.domain

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import upickle.default._

trait CommonRW {
  implicit val dateTimeRW: ReadWriter[OffsetDateTime] = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss XXX")
    readwriter[String].bimap[OffsetDateTime](
      formatter.format(_),
      OffsetDateTime.parse(_, formatter)
    )
  }
}
