package zdtest.repo

import java.io.File

import upickle.default.read
import zdtest.domain.Organisation

object Parser {

  def parseOrgs(f: File): Stream[Organisation] = read[Stream[Organisation]](f)

}
