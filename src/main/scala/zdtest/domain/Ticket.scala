package zdtest.domain

import java.time.OffsetDateTime

import upickle.default.{macroRW, ReadWriter => RW}
import zdtest.repo.Repository
import zdtest.search.Index

case class Ticket(_id: String,
                  created_at: OffsetDateTime,
                  due_at: OffsetDateTime = OffsetDateTime.MIN,
                  url: String = "",
                  external_id: String = "",
                  `type`: String = "",
                  subject: String = "",
                  description: String = "",
                  priority: String = "",
                  status: String = "",
                  submitter_id: Long = -1,
                  assignee_id: Long = -1,
                  organization_id: Long = -1,
                  tags: Set[String] = Set.empty,
                  has_incidents: Boolean = false,
                  via: String = "") extends Searchable {

  override def fullDescription(repo: Repository, index: Index): String = {
    s"""_id:             ${_id}
       |created_at:      ${DateFormat.formatter.format(created_at)}
       |due_at:          ${DateFormat.formatter.format(due_at)}
       |url:             $url
       |external_id:     $external_id
       |subject:         $subject
       |description:     $description
       |priority:        $priority
       |status:          $status
       |submitter:       ${repo.users.get(submitter_id).map(_.shortDescription).getOrElse(s"[not found] - #$submitter_id")}
       |assignee:        ${repo.users.get(assignee_id).map(_.shortDescription).getOrElse(s"[not found] - #$assignee_id")}
       |organization:    ${repo.organisations.get(organization_id).map(_.shortDescription).getOrElse(s"[not found] - #$organization_id")}
       |tags:            ${tags.mkString(", ")}
       |has_incidents:   $has_incidents
       |via:             $via
     """.stripMargin
  }

  override def shortDescription: String = s"""ticket ${_id} - "$subject""""
}

object Ticket extends CommonRW {
  implicit val rw: RW[Ticket] = macroRW
}