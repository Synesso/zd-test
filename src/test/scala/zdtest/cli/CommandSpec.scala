package zdtest.cli

import org.scalacheck.{Arbitrary, Gen}
import org.specs2.mutable.Specification
import zdtest.cli.Command.{Fields, Help, Quit, Search}
import zdtest.domain.{ArbitraryInput, Category, UserCat}

class CommandSpec extends Specification with ArbitraryInput {

  "parsing a command" should {
    "parse an empty string to nothing" >> {
      Command("") must beNone
    }

    "parse 'quit' to Quit" >> {
      Command("quit") must beSome[Command](Quit)
    }

    "parse 'q' to Quit" >> {
      Command("q") must beSome[Command](Quit)
    }

    "parse 'help' to Help" >> {
      Command("help") must beSome[Command](Help)
    }

    "parse 'h' to Help" >> {
      Command("h") must beSome[Command](Help)
    }

    "parse 'fields' to Fields" >> {
      Command("fields") must beSome[Command](Fields)
    }

    "parse 'search' with the correct quantity of parameters" >> prop { (cat: Category[_], field: String, term: String) =>
      Command(s"search ${cat.name} $field $term") must beSome[Command](Search(cat, field, term))
    }.setGen2(Gen.identifier).setGen3(Gen.identifier)

    "parse 'search' with the multilingual terms" >> {
      Command(s"search user name 鈴木花子") must beSome[Command](Search(UserCat, "name", "鈴木花子"))
      Command(s"search user name Étienne Cœur") must beSome[Command](Search(UserCat, "name", "Étienne Cœur"))
    }

    "fail to parse 'search' with invalid category" >> {
      Command("search foo bar baz") must beNone
    }

    "fail to parse 'search' with too few parameters" >> {
      Command("search") must beNone
      Command("search user") must beNone
    }

    "parse 'search' with empty term" >> {
      Command("search user name") must beSome[Command](Search(UserCat, "name", ""))
    }

    "parse 'search' with multi-word term" >> {
      Command("search user name Hickory Diggins") must beSome[Command](Search(UserCat, "name", "Hickory Diggins"))
    }
  }

}
