package zdtest.domain

import java.time.OffsetDateTime

import upickle.default.{macroRW, ReadWriter => RW}

case class User(id: Long,
                url: String,
                externalId: String,
                name: String,
                alias: String,
                createdAt: OffsetDateTime,
                active: Boolean,
                verified: Boolean,
                shared: Boolean,
                locale: String,
                timezone: String,
                lastLoginAt: OffsetDateTime,
                email: String,
                phone: String,
                signature: String,
                organizationId: Long,
                tags: Seq[String],
                suspended: Boolean,
                role: String)

object User extends CommonRW {
  implicit val rw: RW[User] = macroRW
}