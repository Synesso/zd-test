package zdtest.domain

import java.time.OffsetDateTime

import upickle.default.{macroRW, ReadWriter => RW}
import zdtest.repo.Repository
import zdtest.search.Index

case class User(_id: Long,
                created_at: OffsetDateTime,
                last_login_at: OffsetDateTime,
                url: String = "",
                external_id: String = "",
                name: String = "",
                alias: String = "",
                active: Boolean = false,
                verified: Boolean = false,
                shared: Boolean = false,
                locale: String = "",
                timezone: String = "",
                email: String = "",
                phone: String = "",
                signature: String = "",
                organization_id: Long = -1,
                tags: Set[String] = Set.empty,
                suspended: Boolean = false,
                role: String = "") extends Searchable {

  override def fullDescription(repo: Repository, index: Index): String = {

    val ticketsOfUser = (
      index.searchTickets("submitter_id", _id.toString) ++ index.searchTickets("assignee_id", _id.toString)
    ).distinct.map(_.shortDescription).sorted

    s"""_id:             ${_id}
       |created_at:      ${DateFormat.formatter.format(created_at)}
       |last_login_at:   ${DateFormat.formatter.format(last_login_at)}
       |url:             $url
       |external_id:     $external_id
       |name:            $name
       |alias:           $alias
       |active:          $active
       |verified:        $verified
       |shared:          $shared
       |locale:          $locale
       |timezone:        $timezone
       |email:           $email
       |phone:           $phone
       |signature:       $signature
       |organization:    ${repo.organisations.get(organization_id).map(_.shortDescription).getOrElse(s"[not found] - #$organization_id")}
       |tags:            ${tags.mkString(", ")}
       |suspended:       $suspended
       |role:            $role
       |related tickets: ${ticketsOfUser.mkString(System.lineSeparator + "                 ")}
     """.stripMargin
  }

  override def shortDescription: String = s"$name ($alias) - user #${_id}"
}

object User extends CommonRW {
  implicit val rw: RW[User] = macroRW
}