package zdtest.repo

import java.io.File

import upickle.default._
import zdtest.domain.{Organisation, Ticket, User}

import scala.util.{Failure, Success, Try}

object Parser {

  def parseOrgs(f: File): Stream[Organisation] = readFile[Organisation](f)

  def parseUsers(f: File): Stream[User] = readFile[User](f)

  def parseTickets(f: File): Stream[Ticket] = readFile[Ticket](f)

  private def readFile[T](f: File)(implicit r: Reader[Stream[T]]): Stream[T] = Try(read[Stream[T]](f)) match {
    case Success(stream) => stream
    case Failure(t) => throw UnparseableFileException(f, t)
  }

}

case class UnparseableFileException(f: File, cause: Throwable)
  extends RuntimeException(s"Unable to parse ${f.getAbsolutePath}", cause)