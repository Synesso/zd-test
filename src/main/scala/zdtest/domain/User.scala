package zdtest.domain

import java.time.OffsetDateTime

import upickle.default.{macroRW, ReadWriter => RW}

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
                tags: Seq[String] = Nil,
                suspended: Boolean = false,
                role: String = "")

object User extends CommonRW {
  implicit val rw: RW[User] = macroRW
}