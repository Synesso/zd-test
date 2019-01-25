package zdtest.repo

import java.io.File

import zdtest.domain.{Organisation, User}

import scala.concurrent.{ExecutionContext, Future}

class Repository(val organisations: Map[Long, Organisation] = Map.empty,
                 val users: Map[Long, User] = Map.empty)

object Repository {

  // todo - no duplicates in the seqs.

  def apply(orgList: Seq[Organisation] = Nil,
            userList: Seq[User] = Nil): Repository = {

    val orgMap = orgList.map(o => o._id -> o).toMap

    userList.find(u => u.organization_id != -1 && !orgMap.contains(u.organization_id)).foreach { u =>
      throw new IllegalArgumentException(s"User links to non-existent Organisation: $u")
    }

    val userMap = userList.map(u => u._id -> u).toMap

    new Repository(orgMap, userMap)
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

    //    val ticketsFile = file("tickets.json")

    val organisationSeq = Future(Parser.parseOrgs(file("organizations.json")))
    val userSeq = Future(Parser.parseUsers(file("users.json")))

    for {
      orgs <- organisationSeq
      users <- userSeq
    } yield Repository(orgs, users)
  }

}