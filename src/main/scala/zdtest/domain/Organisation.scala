package zdtest.domain

import java.time.OffsetDateTime

import zdtest.repo.Repository
import zdtest.search.Index

case class Organisation(_id: Long,
                        created_at: OffsetDateTime,
                        url: String = "",
                        external_id: String = "",
                        name: String = "",
                        domain_names: Seq[String] = Nil,
                        details: String = "",
                        shared_tickets: Boolean = false,
                        tags: Set[String] = Set.empty) extends Searchable {

  override def fullDescription(repo: Repository, index: Index): String = {
    val usersOfOrg = index.searchUsers("organization_id", _id.toString)
      .filter(_.organization_id == _id).map(_.shortDescription).sorted
    val relatedUsers = if (usersOfOrg.isEmpty) "none" else usersOfOrg.mkString(System.lineSeparator + "                 ")

    s"""_id:             ${_id}
       |created_at:      ${DateFormat.formatter.format(created_at)}
       |url:             $url
       |external_id:     $external_id
       |name:            $name
       |domain_names:    ${domain_names.mkString(", ")}
       |details:         $details
       |shared_tickets:  $shared_tickets
       |tags:            ${tags.mkString(", ")}
       |related users:   $relatedUsers
     """.stripMargin
  }

  override def shortDescription: String = s"${ if (name == "") "[no name]" else name} - #${_id}"
}

object Organisation extends CommonRW {
  import upickle.default._

  implicit val rw: ReadWriter[Organisation] = macroRW[Organisation]
}

