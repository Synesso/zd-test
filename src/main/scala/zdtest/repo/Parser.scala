package zdtest.repo

import java.io.File

import upickle.default.read
import zdtest.domain.{Organisation, User}

object Parser {

  def parseOrgs(f: File): Stream[Organisation] = read[Stream[Organisation]](f)

  def parseUsers(f: File): Stream[User] = read[Stream[User]](f)

}
