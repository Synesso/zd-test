package zdtest.search

import scala.collection.mutable

case class Trie(sub: Map[Char, Trie] = Map.empty, res: Set[Long] = Set.empty) {
}

class TrieBuilder {
  private val sub = mutable.Map.empty[Char, TrieBuilder]
  private val res = Set.newBuilder[Long]

  def add(key: String, value: Long): TrieBuilder = {
    (0 until key.length).map(key.drop).foreach(addInner(_, value))
    this
  }

  private[search] def addInner(key: String, value: Long): TrieBuilder = key match {
    case "" =>
      res += value
      this
    case _ =>
      sub.getOrElseUpdate(key.head, new TrieBuilder).addInner(key.tail, value)
      this
  }

  def build: Trie = {
    Trie(sub.mapValues(_.build).toMap[Char, Trie], res.result())
  }
}

