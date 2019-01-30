package zdtest

import java.io.File
import java.util.Locale

import zdtest.cli.Command
import zdtest.cli.Command._
import zdtest.domain.{Category, OrgCat, TicketCat, UserCat}
import zdtest.repo.Repository
import zdtest.search.Index

import scala.annotation.tailrec
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object ZDSearch {

  def main(args: Array[String]): Unit = {
    val resultCode = startApp(input = scala.io.StdIn.readLine, args).map(_ => 0).recover { case t =>
      System.err.println(s"Unable to initialise: ${t.getMessage}")
      System.err.println(usageMessage)
      -1
    }
    System.exit(Await.result(resultCode, Duration.Inf))
  }

  private[zdtest] def startApp(input: => String, args: Array[String]): Future[Unit] = {
    val dir = new File(args.headOption.getOrElse("."))
    println(s"Loading from ${dir.getAbsolutePath}")
    (for {
      repo <- Repository.fromDir(dir)
      _ = println(s"Indexing ${repo.users.size} users, ${repo.organisations.size} organizations and ${repo.tickets.size} tickets.")
      index <- repo.index
    } yield (repo, index)).map { case (repo: Repository, index: Index) =>
      println(
        s"""Welcome to Zendesk Search.
           |
           |Looking for user input: 'search ...', 'fields', 'help' or 'quit'.
         """.stripMargin)
      promptLoop(input, repo, index)
    }
  }

  private[zdtest] def promptLoop(readUserLine: => String, repo: Repository, index: Index,
                                 act: String => Unit = println, prompt: String => Unit = print): Unit = {
    @tailrec
    def loop(): Unit = {
      prompt("> ")
      Option(readUserLine).flatMap(Command(_)) match {
        case Some(Quit) =>
        case other =>
          other match {
            case Some(NoOp) =>
            case Some(Help) => act(helpMessage)
            case Some(Fields) => act(fieldsMessage)
            case Some(Search(OrgCat, field, term)) => actOnResults(index.searchOrgs(field, term).map(_.fullDescription(repo, index)))
            case Some(Search(UserCat, field, term)) => actOnResults(index.searchUsers(field, term).map(_.fullDescription(repo, index)))
            case Some(Search(TicketCat, field, term)) => actOnResults(index.searchTickets(field, term).map(_.fullDescription(repo, index)))
            case _ => act("command not recognised")
          }
          loop()
      }
    }

    def actOnResults(rs: Seq[String]): Unit = {
      act(s"${rs.size} result${if (rs.size == 1) "" else "s"}")
      act("")
      rs.foreach(act)
    }

    loop()
  }

  val helpMessage: String = {
    """Choose from the following commands:
      |
      |* search
      |Prints elements from the given category where the field partially or fully matches the given term.
      |Category must be one of {'user', 'ticket', 'organisation'}.
      |Field must be a valid field for the category (see `fields`).
      |Term is a case insensitive-match on the prefix of any word in value of the field.
      |   Term may be omitted, in which case it explicitly searches for missing or empty values.
      |
      |  search <category> <field> [term]
      |
      |----------
      |* fields
      |Prints the set of available fields per category.
      |
      |----------
      |* quit
      |Exits the interactive user loop.
      |
      |----------
      |* help
      |Prints this message.
    """.stripMargin
  }

  val fieldsMessage: String = {
    Seq("User", "Organisation", "Ticket").flatMap { s =>
      Category.withName(s.toLowerCase(Locale.ROOT)).map(s -> _)
    }.map { case (s, cat) =>
      val fieldsString = cat.fields.keys.toSeq.sorted.map("* " + _).mkString(System.lineSeparator())

      s"""
         |Search $s with one of
         |===========================
         |$fieldsString
       """.stripMargin
    }.mkString(System.lineSeparator())
  }

  val usageMessage: String = {
    """
      |Usage: java -jar <assembly_jar> [data_dir]
      |
      |   assembly_jar - the result of running `sbt assembly`
      |   data_dir     - the directory containing the data files organizations.json, tickets.json & users.json
      |                  if omitted, `.` is assumed.
      |
    """.stripMargin
  }
}
