package zdtest.domain

import java.time._
import java.time.temporal.ChronoUnit

import org.scalacheck.{Arbitrary, Gen}
import org.specs2.ScalaCheck

trait ArbitraryInput extends ScalaCheck {

  def genOrg: Gen[Organisation] = for {
    id <- Gen.posNum[Long]
    url <- Gen.identifier
    externalId <- Gen.identifier
    name <- Gen.identifier
    domainNames <- Gen.listOf(Gen.identifier)
    createdAt <- genOffsetDateTime
    details <- Gen.identifier
    sharedTickets <- genBool
    tags <- Gen.listOf(Gen.identifier)
  } yield Organisation(id, url, externalId, name, domainNames, createdAt, details, sharedTickets, tags)

  implicit val arbOrg: Arbitrary[Organisation] = Arbitrary(genOrg)

  def genUser: Gen[User] = for {
    id <- Gen.posNum[Long]
    url <- Gen.identifier
    external_id <- Gen.identifier
    name <- Gen.identifier
    alias <- Gen.identifier
    created_at <- genOffsetDateTime
    active <- genBool
    verified <- genBool
    shared <- genBool
    locale <- Gen.identifier
    timezone <- Gen.identifier
    last_login_at <- genOffsetDateTime
    email <- Gen.identifier
    phone <- Gen.identifier
    signature <- Gen.identifier
    organization_id <- Gen.option(Gen.posNum[Long]).map(_.getOrElse(-1L))
    tags <- Gen.listOf(Gen.identifier)
    suspended <- genBool
    role <- Gen.identifier
  } yield User(id, created_at, last_login_at, url, external_id, name, alias, active, verified, shared, locale, timezone,
    email, phone, signature, organization_id, tags, suspended, role)

  implicit val arbUser: Arbitrary[User] = Arbitrary(genUser)

  def genOffsetDateTime: Gen[OffsetDateTime] = for {
    offset <- Gen.choose(-11, 11).map(ZoneOffset.ofHours)
    instant <- Gen.posNum[Long].map(Instant.ofEpochMilli).map(_.truncatedTo(ChronoUnit.SECONDS))
  } yield OffsetDateTime.ofInstant(instant, offset)

  def genBool: Gen[Boolean] = Gen.oneOf(true, false)

}
