package zdtest.repo

import java.io.File
import java.time.{OffsetDateTime, ZoneOffset}

import org.specs2.mutable.Specification
import zdtest.domain.{Organisation, User}

class ParserSpec extends Specification {

  "parsing organisation files" should {
    "parse an empty list" >> {
      Parser.parseOrgs(file("empty_list")) must beEmpty
    }

    "parse a single organisation" >> {
      Parser.parseOrgs(file("single_org")) mustEqual Seq(
        Organisation(
          _id = 106,
          url = "http://initech.zendesk.com/api/v2/organizations/106.json",
          external_id = "2355f080-b37c-44f3-977e-53c341fde146",
          name = "Qualitern",
          domain_names = Seq("gology.com", "myopium.com", "synkgen.com", "bolax.com"),
          created_at = OffsetDateTime.of(2016, 7, 23, 9, 48, 2, 0, ZoneOffset.ofHours(-10)),
          details = "Artisân",
          shared_tickets = false,
          tags = Seq("Nolan", "Rivas", "Morse", "Conway")
        )
      )
    }

    "parse multiple organisations" >> {
      Parser.parseOrgs(file("many_orgs")) must beLike[Seq[Organisation]] { case xs =>
        xs.size mustEqual 25
        xs.last mustEqual Organisation(
          _id = 125,
          url = "http://initech.zendesk.com/api/v2/organizations/125.json",
          external_id = "42a1a845-70cf-40ed-a762-acb27fd606cc",
          name = "Strezzö",
          domain_names = Seq("techtrix.com", "teraprene.com", "corpulse.com", "flotonic.com"),
          created_at = OffsetDateTime.of(2016, 2, 21, 6, 11, 51, 0, ZoneOffset.ofHours(-11)),
          details = "MegaCorp",
          shared_tickets = false,
          tags = Seq("Vance", "Ray", "Jacobs", "Frank")
        )
      }
    }
  }

  "parsing user files" should {
    "parse an empty list" >> {
      Parser.parseUsers(file("empty_list")) must beEmpty
    }

    "parse a single user" >> {
      Parser.parseUsers(file("single_user")) mustEqual Seq(
        User(
          _id = 66,
          created_at = OffsetDateTime.of(2016, 4, 11, 10, 8, 8, 0, ZoneOffset.ofHours(-10)),
          last_login_at = OffsetDateTime.of(2014, 3, 18, 5, 42, 21, 0, ZoneOffset.ofHours(-11)),
          url = "http://initech.zendesk.com/api/v2/users/66.json",
          external_id = "e29c3611-d1f2-492e-a805-594e239ff922",
          name = "Geneva Poole",
          alias = "Mr Fernandez",
          active = true,
          verified = true,
          shared = true,
          locale = "en-AU",
          timezone = "Aruba",
          email = "fernandezpoole@flotonic.com",
          phone = "8925-633-579",
          signature = "Don't Worry Be Happy!",
          organization_id = 114,
          tags = Seq("Whitehaven", "Omar", "Waiohinu", "Catharine"),
          suspended = true,
          role = "admin")
      )
    }

    "parse multiple users" >> {
      Parser.parseUsers(file("many_users")) must beLike[Seq[User]] { case xs =>
        xs.size mustEqual 75
        xs.last mustEqual User(
          _id = 75,
          created_at = OffsetDateTime.of(2016, 6, 7, 9, 18, 0, 0, ZoneOffset.ofHours(-10)),
          last_login_at = OffsetDateTime.of(2012, 10, 15, 12, 36, 41, 0, ZoneOffset.ofHours(-11)),
          url = "http://initech.zendesk.com/api/v2/users/75.json",
          external_id = "0db0c1da-8901-4dc3-a469-fe4b500d0fca",
          name = "Catalina Simpson",
          alias = "Miss Rosanna",
          verified = true,
          shared = true,
          locale = "zh-CN",
          timezone = "US Minor Outlying Islands",
          email = "rosannasimpson@flotonic.com",
          phone = "8615-883-099",
          signature = "Don't Worry Be Happy!",
          organization_id = 119,
          tags = Seq("Veguita", "Navarre", "Elizaville", "Beaulieu"),
          suspended = true,
          role = "agent"
        )
      }
    }
  }

  private def file(name: String): File = new File(s"src/test/resources/repo/$name.json")

}