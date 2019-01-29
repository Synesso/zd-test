package zdtest.search

import org.apache.commons.collections4.Trie
import org.apache.commons.collections4.trie.PatriciaTrie
import zdtest.domain._

import scala.concurrent.{ExecutionContext, Future}
import scala.collection.JavaConverters._

class Index(orgs: Map[String, Trie[String, Organisation]],
            users: Map[String, Trie[String, User]],
            tickets: Map[String, Trie[String, Ticket]]) {


  def search(cat: Category[_], field: String, term: String): Seq[Searchable] = {
    cat match {
      case OrgCat => orgs.get(field).toSeq.flatMap(_.prefixMap(term).asScala.values).distinct
      case UserCat => users.get(field).toSeq.flatMap(_.prefixMap(term).asScala.values).distinct
      case TicketCat => tickets.get(field).toSeq.flatMap(_.prefixMap(term).asScala.values).distinct
    }
  }
}

object Index {

  def build(organisations: Iterable[Organisation] = Nil,
            users: Iterable[User] = Nil,
            tickets: Iterable[Ticket] = Nil)(implicit ec: ExecutionContext): Future[Index] = {

    def trie[T](ts: Iterable[T], key: T => String): Future[Trie[String, T]] = Future {
      val variants = ts.flatMap { t =>
        val k = key(t)
        val ks = (0 until k.length).map(k.drop)
        ks.map(_ -> t)
      }.toMap.asJava
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

