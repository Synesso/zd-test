package zdtest.domain

sealed trait Category[T] {
  val name: String
  val fields: Map[String, T => String]
}

case object UserCat extends Category[User] {
  val name = "user"
  val fields: Map[String, User => String] = Map(
    "_id" -> (_._id.toString),
    "url" -> (_.url),
    "external_id" -> (_.external_id),
    "name" -> (_.name),
    "alias" -> (_.alias),
    "created_at" -> (u => DateFormat.formatter.format(u.created_at)),
    "active" -> (_.active.toString),
    "verified" -> (_.verified.toString),
    "shared" -> (_.shared.toString),
    "locale" -> (_.locale),
    "timezone" -> (_.timezone),
    "last_login_at" -> (u => DateFormat.formatter.format(u.last_login_at)),
    "email" -> (_.email),
    "phone" -> (_.phone),
    "signature" -> (_.signature),
    "organization_id" -> (_.organization_id.toString),
    "tags" -> (_.tags.mkString(" ")),
    "suspended" -> (_.suspended.toString),
    "role" -> (_.role)
  )
}

case object OrgCat extends Category[Organisation] {
  val name = "organisation"
  val fields: Map[String, Organisation => String] = Map(
    "_id" -> (_._id.toString),
    "url" -> (_.url),
    "external_id" -> (_.external_id.toString),
    "name" -> (_.name),
    "domain_names" -> (_.domain_names.mkString(" ")),
    "created_at" -> (o => DateFormat.formatter.format(o.created_at)),
    "details" -> (_.details),
    "shared_tickets" -> (_.shared_tickets.toString),
    "tags" -> (_.tags.mkString(" "))
  )
}

case object TicketCat extends Category[Ticket] {
  val name = "ticket"
  val fields: Map[String, Ticket => String] = Map(
    "_id" -> (_._id),
    "url" -> (_.url),
    "external_id" -> (_.external_id),
    "created_at" -> (t => DateFormat.formatter.format(t.created_at)),
    "type" -> (_.`type`),
    "subject" -> (_.subject),
    "description" -> (_.description),
    "priority" -> (_.priority),
    "status" -> (_.status),
    "submitter_id" -> (_.submitter_id.toString),
    "assignee_id" -> (_.assignee_id.toString),
    "organization_id" -> (_.organization_id.toString),
    "tags" -> (_.tags.mkString(" ")),
    "has_incidents" -> (_.has_incidents.toString),
    "due_at" -> (t => DateFormat.formatter.format(t.due_at)),
    "via" -> (_.via),
  )
}

object Category {
  private val cats: Map[String, Category[_]] = Map(
    "user" -> UserCat,
    "organisation" -> OrgCat,
    "organization" -> OrgCat,
    "org" -> OrgCat,
    "ticket" -> TicketCat
  )

  def withName(name: String): Option[Category[_]] = cats.get(name)
}

