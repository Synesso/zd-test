package zdtest.domain

import java.time.ZonedDateTime

import org.scalacheck.{Arbitrary, Gen}
import org.specs2.ScalaCheck

trait ArbitraryInput extends ScalaCheck {

  def genOrg: Gen[Organisation] = for {
    id <- Gen.posNum[Long]
    url <- Gen.identifier
    externalId <- Gen.identifier
    name <- Gen.identifier
    domainNames <- Gen.listOf(Gen.identifier)
    createdAt <- Gen.calendar.map(c => ZonedDateTime.ofInstant(c.toInstant, c.getTimeZone.toZoneId))
    details <- Gen.identifier
    sharedTickets <- Gen.oneOf(true, false)
    tags <- Gen.listOf(Gen.identifier)
  } yield Organisation(id, url, externalId, name, domainNames, createdAt, details, sharedTickets, tags)

  implicit val arbOrg: Arbitrary[Organisation] = Arbitrary(genOrg)

}
