package zdtest.search

import scala.annotation.tailrec
import scala.collection.mutable

case class Trie[V](sub: Map[Char, Trie[V]] = Map.empty[Char, Trie[V]], res: Set[V] = Set.empty[V]) {

  @tailrec
  final def search(term: String): Set[V] = term match {
    case "" => results
    case _ => sub.get(term.head) match {
      case None => Set.empty
      case Some(trie) => trie.search(term.tail)
    }
  }

  private[search] def results: Set[V] = res ++ sub.values.flatMap(_.results)
}

class TrieBuilder[V] {
  private val sub = mutable.Map.empty[Char, TrieBuilder[V]]
  private val res = Set.newBuilder[V]

  def add(key: String, value: V): TrieBuilder[V] = {
    (0 until key.length).map(key.drop).foreach(addInner(_, value))
    this
  }

  private[search] def addInner(key: String, value: V): TrieBuilder[V] = key match {
    case "" =>
      res += value
      this
    case _ =>
      sub.getOrElseUpdate(key.head, new TrieBuilder[V]).addInner(key.tail, value)
      this
  }

  def build: Trie[V] = {
    Trie(sub.mapValues(_.build).toMap[Char, Trie[V]], res.result())
  }
}

