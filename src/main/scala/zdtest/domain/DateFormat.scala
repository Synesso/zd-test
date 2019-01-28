package zdtest.domain

import java.time.format.DateTimeFormatter

object DateFormat {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss XXX")
}
