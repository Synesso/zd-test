package zdtest.repo

import java.io.File

import zdtest.domain.Organisation

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

case class Repository(organisations: Map[Long, Organisation] = Map.empty) {

  def withOrganisations(orgs: Seq[Organisation]): Repository = copy(
    organisations = orgs.foldLeft(organisations) { case (acc, org) =>
        acc.updated(org._id, org)
    }
  )

}

object Repository {

  /**
    * Builds a repository from the data files present at the given directory
    * @param dir directory which must contain readable files users.json, tickets.json & organizations.json
    */
  def apply(dir: File)(implicit ec: ExecutionContext): Future[Repository] = {
    def file(n: String) = {
      val f = new File(dir, n)
      require(f.canRead, s"Unable to read ${f.getAbsolutePath}")
      f
    }


    val orgsFile = file("organizations.json")
//    val usersFile = file("users.json")
//    val ticketsFile = file("tickets.json")

    val organisations = Future(Parser.parseOrgs(orgsFile))

    for {
      orgs <- organisations
    } yield Repository().withOrganisations(orgs)
  }

}