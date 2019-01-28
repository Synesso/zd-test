package zdtest.search

import org.scalacheck.Gen
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

class TrieSpec extends Specification with ScalaCheck {

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
      val trie = kvp.foldLeft(new TrieBuilder()) { case (acc, (k, v)) => acc.add(k, v) }.build
      forall(kvp) { case (key, value) => trie.search(key) must contain(value) }
    }.setGen(Gen.listOf(for {
      key <- Gen.identifier
      value <- Gen.posNum[Long]
    } yield (key, value)))
  }

  "searching a trie" should {
    val trie = new TrieBuilder().add("plasma", 1).add("banana", 2).add("phantasma", 3).build

    "provide full-match results" >> {
      trie.search("plasma") mustEqual Set(1)
    }

    "provide results for partial-match on head" >> {
      trie.search("p") mustEqual Set(1, 3)
    }

    "provide results for partial-match on middle" >> {
      trie.search("an") mustEqual Set(2, 3)
    }

    "provide results for partial-match on tail" >> {
      trie.search("asma") mustEqual Set(1, 3)
    }

    "provide results for match on common term" >> {
      trie.search("a") mustEqual Set(1, 2, 3)
    }

    "provide no results for no match" >> {
      trie.search("quanta") must beEmpty
    }
  }
}
