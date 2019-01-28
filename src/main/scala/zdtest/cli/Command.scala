package zdtest.cli

import zdtest.domain.Category

sealed trait Command

object Command {

  case object NoOp extends Command
  case object Quit extends Command
  case object Help extends Command
  case object Fields extends Command
  case class Search(cat: Category[_], field: String, term: String) extends Command

  def apply(s: String): Option[Command] = {
    s.split("\\s").toSeq match {
      case Seq("") => Some(NoOp)

      case Seq("quit") | Seq("q") => Some(Quit)

      case Seq("help") | Seq("h") => Some(Help)

      case Seq("fields") => Some(Fields)

      case "search" +: cat +: field +: term =>
        Category.withName(cat).map(Search(_, field, term.mkString(" ")))

      case _ => None
    }
  }
}