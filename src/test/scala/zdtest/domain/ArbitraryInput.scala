package zdtest.domain

import java.time._
import java.time.temporal.ChronoUnit

import org.scalacheck.{Arbitrary, Gen}
import org.specs2.ScalaCheck

import scala.collection.JavaConverters._

trait ArbitraryInput extends ScalaCheck {

  def genOrg: Gen[Organisation] = for {
    id <- Gen.posNum[Long]
    url <- Gen.identifier
    externalId <- Gen.identifier
    name <- Gen.identifier
    domainNames <- Gen.listOf(Gen.identifier)
    createdAt <- genOffsetDateTime
    details <- Gen.identifier
    sharedTickets <- Gen.oneOf(true, false)
    tags <- Gen.listOf(Gen.identifier)
  } yield Organisation(id, url, externalId, name, domainNames, createdAt, details, sharedTickets, tags)

  implicit val arbOrg: Arbitrary[Organisation] = Arbitrary(genOrg)

  def genOffsetDateTime: Gen[OffsetDateTime] = for {
    offset <- Gen.choose(-11, 11).map(ZoneOffset.ofHours)
    instant <- Gen.posNum[Long].map(Instant.ofEpochMilli).map(_.truncatedTo(ChronoUnit.SECONDS))
  } yield OffsetDateTime.ofInstant(instant, offset)

}
