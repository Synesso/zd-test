package zdtest.repo

import java.io.File

import zdtest.domain.{Organisation, Ticket, User}
import zdtest.search.Index

import scala.concurrent.{ExecutionContext, Future}

class Repository(val organisations: Map[Long, Organisation] = Map.empty,
                 val users: Map[Long, User] = Map.empty,
                 val tickets: Map[String, Ticket] = Map.empty)(implicit ec: ExecutionContext) {

  // lazy to prevent Future explosion & thread starvation during testing
  private lazy val _index: Future[Index] = Index.build(organisations.values, users.values, tickets.values)

  def index: Future[Index] = _index

}

object Repository {

  def apply(orgList: Seq[Organisation] = Nil,
            userList: Seq[User] = Nil,
            ticketList: Seq[Ticket] = Nil)(implicit ec: ExecutionContext): Repository = {

    val orgMap = orgList.map(o => o._id -> o).toMap
    val userMap = userList.map(u => u._id -> u).toMap
    val ticketMap = ticketList.map(t => t._id -> t).toMap

    new Repository(orgMap, userMap, ticketMap)
  }

  /**
    * Builds a repository from the data files present at the given directory
    * @param dir directory which must contain readable files users.json, tickets.json & organizations.json
    */
  def fromDir(dir: File)(implicit ec: ExecutionContext): Future[Repository] = {
    def file(n: String) = {
      val f = new File(dir, n)
      require(f.canRead, s"Unable to read ${f.getAbsolutePath}")
      f
    }

    val organisationSeq = Future(Parser.parseOrgs(file("organizations.json")))
    val userSeq = Future(Parser.parseUsers(file("users.json")))
    val ticketSeq = Future(Parser.parseTickets(file("tickets.json")))

    for {
      orgs <- organisationSeq
      users <- userSeq
      tickets <- ticketSeq
    } yield Repository(orgs, users, tickets)
  }

}