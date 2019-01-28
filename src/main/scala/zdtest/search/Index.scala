package zdtest.search

import zdtest.domain._

import scala.concurrent.{ExecutionContext, Future}

class Index(orgs: Map[String, Trie[Long]],
            users: Map[String, Trie[Long]],
            tickets: Map[String, Trie[String]]) {



}

object Index {

  def build(organisations: Iterable[Organisation] = Nil,
            users: Iterable[User] = Nil,
            tickets: Iterable[Ticket] = Nil)(implicit ec: ExecutionContext): Future[Index] = {

    def trie[T, V](ts: Iterable[T], value: T => String, id: T => V): Future[Trie[V]] = Future(
      ts.foldLeft(new TrieBuilder[V]()) { case (tb, t) => tb.add(value(t), id(t)) }.build
    )

    val orgTrie = Future.sequence(OrgCat.fields.toSeq.map { case (name, extractor) =>
      trie(organisations, extractor, (_: Organisation)._id).map(name -> _)
    }).map(_.toMap)

    val userTrie = Future.sequence(UserCat.fields.toSeq.map { case (name, extractor) =>
      trie(users, extractor, (_: User)._id).map(name -> _)
    }).map(_.toMap)

    val ticketTrie = Future.sequence(TicketCat.fields.toSeq.map { case (name, extractor) =>
      trie(tickets, extractor, (_: Ticket)._id).map(name -> _)
    }).map(_.toMap)

    for {
      orgs <- orgTrie
      users <- userTrie
      tickets <- ticketTrie
    } yield new Index(orgs, users, tickets)

  }
}

