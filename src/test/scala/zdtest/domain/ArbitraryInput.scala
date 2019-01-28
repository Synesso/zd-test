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
    tags <- Gen.containerOf[Set, String](Gen.identifier)
  } yield Organisation(id, createdAt, url, externalId, name, domainNames, details, sharedTickets, tags)

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
    tags <- Gen.containerOf[Set, String](Gen.identifier)
    suspended <- genBool
    role <- Gen.identifier
  } yield User(id, created_at, last_login_at, url, external_id, name, alias, active, verified, shared, locale, timezone,
    email, phone, signature, organization_id, tags, suspended, role)

  implicit val arbUser: Arbitrary[User] = Arbitrary(genUser)

  def genTicket: Gen[Ticket] = for {
    id <- Gen.identifier
    createdAt <- genOffsetDateTime
    dueAt <- Gen.option(genOffsetDateTime).map(_.getOrElse(OffsetDateTime.MIN))
    url <- Gen.identifier
    external_id <- Gen.identifier
    tpe <- Gen.identifier
    subject <- Gen.identifier
    description  <- Gen.identifier
    priority <- Gen.identifier
    status <- Gen.identifier
    submitter_id <- Gen.option(Gen.posNum[Long]).map(_.getOrElse(-1L))
    assignee_id <- Gen.option(Gen.posNum[Long]).map(_.getOrElse(-1L))
    organization_id <- Gen.option(Gen.posNum[Long]).map(_.getOrElse(-1L))
    tags <- Gen.containerOf[Set, String](Gen.identifier)
    has_incidents <- genBool
    via <- Gen.identifier
  } yield Ticket(id, createdAt, dueAt, url, external_id, tpe, subject, description, priority, status, submitter_id,
    assignee_id, organization_id, tags, has_incidents, via)

  implicit val arbTicket: Arbitrary[Ticket] = Arbitrary(genTicket)

  def genCategory: Gen[Category] = Gen.oneOf(UserCat, OrgCat, TicketCat)

  implicit val arbCategory: Arbitrary[Category] = Arbitrary(genCategory)

  def genNonEmptyString: Gen[String] = Arbitrary.arbString.arbitrary.suchThat(_.nonEmpty)

  def genOffsetDateTime: Gen[OffsetDateTime] = for {
    offset <- Gen.choose(-11, 11).map(ZoneOffset.ofHours)
    instant <- Gen.posNum[Long].map(Instant.ofEpochMilli).map(_.truncatedTo(ChronoUnit.SECONDS))
  } yield OffsetDateTime.ofInstant(instant, offset)

  def genBool: Gen[Boolean] = Gen.oneOf(true, false)

}
