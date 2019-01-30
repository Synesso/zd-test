package zdtest.domain

import java.time.OffsetDateTime

import upickle.default._
import zdtest.repo.Repository
import zdtest.search.Index

trait Searchable {
  def fullDescription(repo: Repository, index: Index): String
  def shortDescription: String
}

trait CommonRW {
  implicit val dateTimeRW: ReadWriter[OffsetDateTime] = {
    readwriter[String].bimap[OffsetDateTime](
      DateFormat.formatter.format(_),
      OffsetDateTime.parse(_, DateFormat.formatter)
    )
  }
}
