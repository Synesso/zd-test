package zdtest.search

import java.util.Locale

import org.apache.commons.collections4.Trie
import org.apache.commons.collections4.trie.PatriciaTrie
import upickle.implicits.key
import zdtest.domain._

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}
import scala.collection.JavaConverters._

class Index(val orgs: Map[String, Trie[String, Seq[Organisation]]],
            users: Map[String, Trie[String, Seq[User]]],
            tickets: Map[String, Trie[String, Seq[Ticket]]]) {


  def search(cat: Category[_], field: String, term: String): Seq[Searchable] = {
    val key = Index.convertKey(term)
    cat match {
      case OrgCat => orgs.get(field).toSeq.flatMap(_.prefixMap(key).asScala.values).flatten.distinct
      case UserCat => users.get(field).toSeq.flatMap(_.prefixMap(key).asScala.values).flatten.distinct
      case TicketCat => tickets.get(field).toSeq.flatMap(_.prefixMap(key).asScala.values).flatten.distinct
    }
  }
}

object Index {

  val EndOfText = "\u0003"

  def convertKey(key: String): String = key match {
    case "" => EndOfText
    case k => k.toLowerCase(Locale.ROOT)
  }

  def build(organisations: Iterable[Organisation] = Nil,
            users: Iterable[User] = Nil,
            tickets: Iterable[Ticket] = Nil)(implicit ec: ExecutionContext): Future[Index] = {

    def trie[T](ts: Iterable[T], key: T => String): Future[Trie[String, Seq[T]]] = Future {
      val variants = ts.flatMap { t =>
        @tailrec
        def dropWords(s: String, ws: Set[String] = Set.empty): Set[String] = {
          s.trim match {
            case "" => ws
            case trimmed =>
              val (word, tail) = trimmed.span(c => !Character.isWhitespace(c))
              dropWords(tail, ws + word)
          }
        }
        convertKey(key(t)) match {
          case EndOfText => Set(EndOfText -> t) // special handling for empty values
          case k => dropWords(k).map(_ -> t) // create a separate entry for each trailing sub-sequence of words
        }
      }.foldLeft(Map.empty[String, Seq[T]]) { case (map, (k, v)) =>
          map.updated(k, v +: map.getOrElse(k, Seq.empty[T]))
      }.asJava
      new PatriciaTrie(variants)
    }

    val orgTrie = Future.sequence(OrgCat.fields.toSeq.map { case (name, extractor) =>
      trie(organisations, extractor).map(name -> _)
    }).map(_.toMap)

    val userTrie = Future.sequence(UserCat.fields.toSeq.map { case (name, extractor) =>
      trie(users, extractor).map(name -> _)
    }).map(_.toMap)

    val ticketTrie = Future.sequence(TicketCat.fields.toSeq.map { case (name, extractor) =>
      trie(tickets, extractor).map(name -> _)
    }).map(_.toMap)

    for {
      orgs <- orgTrie
      users <- userTrie
      tickets <- ticketTrie
    } yield new Index(orgs, users, tickets)

  }
}

