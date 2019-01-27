package zdtest.cli

sealed trait Category {
  val name: String
  val fields: Seq[String]
}

case object UserCat extends Category {
  val name = "user"
  val fields: Seq[String] = Seq(
    "_id",
    "url",
    "external_id",
    "name",
    "alias",
    "created_at",
    "active",
    "verified",
    "shared",
    "locale",
    "timezone",
    "last_login_at",
    "email",
    "phone",
    "signature",
    "organization_id",
    "tags",
    "suspended",
    "role")
}

case object OrgCat extends Category {
  val name = "organisation"
  val fields: Seq[String] = Seq(
    "_id",
    "url",
    "external_id",
    "name",
    "domain_names",
    "created_at",
    "details",
    "shared_tickets",
    "tags")
}

case object TicketCat extends Category {
  val name = "ticket"
  val fields: Seq[String] = Seq(
    "_id",
    "url",
    "external_id",
    "created_at",
    "type",
    "subject",
    "description",
    "priority",
    "status",
    "submitter_id",
    "assignee_id",
    "organization_id",
    "tags",
    "has_incidents",
    "due_at",
    "via")
}

object Category {
  private val cats: Map[String, Category] = Map(
    "user" -> UserCat,
    "organisation" -> OrgCat,
    "organization" -> OrgCat,
    "org" -> OrgCat,
    "ticket" -> TicketCat
  )

  def withName(name: String): Option[Category] = cats.get(name)
}

