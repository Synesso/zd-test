package zdtest.search

import zdtest.domain.Category

import scala.collection.concurrent.TrieMap

object Index {

  def main(args: Array[String]): Unit = {
  }

}

class Index {

  private val data: Map[Category[_], Map[String, Trie]] = Map.empty

}
