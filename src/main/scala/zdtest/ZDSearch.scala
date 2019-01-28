package zdtest

import java.io.File
import java.util.Locale

import zdtest.cli.Command.{Fields, Help, Quit, Search}
import zdtest.cli.Command
import zdtest.domain.Category
import zdtest.repo.Repository

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object ZDSearch {

  def main(args: Array[String]): Unit = {
    val userLoop = Repository.fromDir(new File(args.headOption.getOrElse("."))).map { repo =>
      println(
        s"""Welcome to Zendesk Search.
           |Indexed ${repo.users.size} users, ${repo.organisations.size} organizations and ${repo.tickets.size} tickets.
           |
           |Looking for user input: 'search ...', 'fields', 'help' or 'quit'.
         """.stripMargin)
      promptLoop(scala.io.StdIn.readLine(), repo)
    }

    val resultCode = userLoop.map(_ => 0).recover { case t =>
      System.err.println(s"Unable to initialise: ${t.getMessage}")
      System.err.println(usageMessage)
      -1
    }

    System.exit(Await.result(resultCode, Duration.Inf))
  }

  def promptLoop(readUserLine: => String, repo: Repository, act: String => Unit = println, prompt: String => Unit = println): Unit = {
    @tailrec
    def loop(): Unit = {
      prompt("> ")
      Option(readUserLine).flatMap(Command(_)) match {
        case Some(Quit) =>
        case other =>
          other match {
            case Some(Help) => act(helpMessage)
            case Some(Fields) => act(fieldsMessage)
            case Some(s: Search) => repo.users.map(_.toString).foreach(act)
            case _ => act("command not recognised")
          }
          loop()
      }
    }
    loop()
  }

  val helpMessage: String = {
      """Choose from the following commands:
        |
        |* search
        |Prints elements from the given category where the field partially or fully matches the value. The
        |Category must be one of {'user', 'ticket', 'organisation'}.
        |
        |  search <category> <field> <value>
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
      val fieldsString = cat.fields.sorted.map("* " + _).mkString(System.lineSeparator())

      s"""
         |Search $s with one of
         |===========================
         |$fieldsString
       """.stripMargin
    }.mkString(System.lineSeparator())
  }

  val usageMessage: String = {
    """
      |Usage: java -jar <assembly_jar> <data_dir>
      |
      |   assembly_jar - the result of running `sbt assembly`
      |   data_dir     - the directory containing the data files organizations.json, tickets.json & users.json
      |
    """.stripMargin
  }
}