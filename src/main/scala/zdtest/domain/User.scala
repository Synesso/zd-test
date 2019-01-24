package zdtest.domain

import upickle.default.{ReadWriter => RW, macroRW}

case class User(id: Int,
                url: String,
                externalId: String,
                name: String,
                alias: String,
                createdAt: String,
                active: Boolean,
                verified: Boolean,
                shared: Boolean,
                locale: String,
                timezone: String,
                lastLoginAt: String,
                email: String,
                phone: String,
                signature: String,
                organizationId: Int, // parse to organization - or fail
                tags: Seq[String],
                suspended: Boolean,
                role: String
               )

object User {
  implicit val rw: RW[User] = macroRW
}