package zdtest.cli

import java.io.File

object Params {
  def apply(args: Array[String]): Either[String, Params] = args match {

    case Array("--dir", dirString, cat, field, term) =>
      val dir = new File(dirString)
      if (!dir.isDirectory || !dir.canRead) Left(s"Not a valid path: ${dir.getCanonicalPath}")
      else params(dir, cat, field, term)

    case Array(cat, field, term) => params(new File("."), cat, field, term)

    case _ => Left("Unrecognised parameters")
  }

  private def params(dir: File, cat: String, field: String, term: String): Either[String, Params] = {
    Category.withName(cat) match {
      case None => Left(s"Invalid category: $cat")
      case Some(c) if c.fields.contains(field) => Right(Params(dir, c, field, term))
      case Some(c) => Left(s"Field $field is invalid for category $cat")
    }
  }
}

case class Params(dir: File, category: Category, field: String, term: String)



