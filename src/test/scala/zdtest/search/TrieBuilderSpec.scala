package zdtest.search

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

class TrieBuilderSpec extends Specification with ScalaCheck {

  "a builder" should {
    "create an empty trie when nothing is added" >> {
      new TrieBuilder().build mustEqual Trie()
    }

    "create a trie with a single character entry" >> {
      new TrieBuilder().add("x", 88).build mustEqual Trie(sub = Map('x' -> Trie(res = Set(88))))
    }

    "create a trie with a single word entry" >> {
      new TrieBuilder().add("hey", 99).build mustEqual Trie(
        sub = Map(
          'h' -> Trie(sub = Map('e' -> Trie(sub = Map('y' -> Trie(res = Set(99)))))),
          'e' -> Trie(sub = Map('y' -> Trie(res = Set(99)))),
          'y' -> Trie(res = Set(99)),
        )
      )
    }

    "create a trie with a multiple overlapping word entries" >> {
      new TrieBuilder().add("wood", 9).add("would", 8).add("could", 7).build mustEqual Trie(
        sub = Map(
          'c' -> Trie(sub = Map(
            'o' -> Trie(sub = Map(
              'u' -> Trie(sub = Map(
                'l' -> Trie(sub = Map(
                  'd' -> Trie(res = Set(7))
                )),
              )),
            ))
          )),
          'w' -> Trie(sub = Map(
            'o' -> Trie(sub = Map(
              'o' -> Trie(sub = Map(
                'd' -> Trie(res = Set(9))
              )),
              'u' -> Trie(sub = Map(
                'l' -> Trie(sub = Map(
                  'd' -> Trie(res = Set(8))
                )),
              )),
            ))
          )),
          'o' -> Trie(sub = Map(
            'o' -> Trie(sub = Map(
              'd' -> Trie(res = Set(9))
            )),
            'u' -> Trie(sub = Map(
              'l' -> Trie(sub = Map(
                'd' -> Trie(res = Set(7, 8))
              )),
            )),
            'd' -> Trie(res = Set(9))
          )),
          'u' -> Trie(sub = Map(
            'l' -> Trie(sub = Map(
              'd' -> Trie(res = Set(7, 8))
            )),
          )),
          'l' -> Trie(sub = Map(
            'd' -> Trie(res = Set(7, 8))
          )),
          'd' -> Trie(res = Set(7, 8, 9))
        )
      )
    }

    "create a trie with arbitrary keys and values" >> prop { kvp: Seq[(String, Long)] =>
//      val trie =
        kvp.foldLeft(new TrieBuilder()) { case (acc, (k, v)) => acc.add(k, v) }.build must not(throwAn[Exception])
//      forall(kvp) { case (key, value) => trie.search(key) must contain(value) } // todo

    }
  }


}
