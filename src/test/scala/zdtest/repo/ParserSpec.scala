package zdtest.repo

import java.io.File
import java.time.{OffsetDateTime, ZoneOffset}

import org.specs2.mutable.Specification
import zdtest.domain.{Organisation, Ticket, User}

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
          tags = Set("Nolan", "Rivas", "Morse", "Conway")
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
          tags = Set("Vance", "Ray", "Jacobs", "Frank")
        )
      }
    }

    "fail to parse a file with non-json content" >> {
      Parser.parseOrgs(file("not")) must throwAn[UnparseableFileException]
    }

    "fail to parse a file with incorrect json content" >> {
      Parser.parseOrgs(file("single_ticket")) must throwAn[UnparseableFileException]
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
          tags = Set("Whitehaven", "Omar", "Waiohinu", "Catharine"),
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
          tags = Set("Veguita", "Navarre", "Elizaville", "Beaulieu"),
          suspended = true,
          role = "agent"
        )
      }
    }

    "fail to parse a file with non-json content" >> {
      Parser.parseUsers(file("not")) must throwAn[UnparseableFileException]
    }

    "fail to parse a file with incorrect json content" >> {
      Parser.parseUsers(file("single_org")) must throwAn[UnparseableFileException]
    }
  }

  "parsing ticket files" should {
    "parse an empty list" >> {
      Parser.parseTickets(file("empty_list")) must beEmpty
    }

    "parse a single ticket" >> {
      Parser.parseTickets(file("single_ticket")) mustEqual Seq(
        Ticket(
          _id = "1a227508-9f39-427c-8f57-1b72f3fab87c",
          url = "http://initech.zendesk.com/api/v2/tickets/1a227508-9f39-427c-8f57-1b72f3fab87c.json",
          external_id = "3e5ca820-cd1f-4a02-a18f-11b18e7bb49a",
          created_at = OffsetDateTime.of(2016, 4, 14, 8, 32, 31, 0, ZoneOffset.ofHours(-10)),
          `type` = "incident",
          subject = "A Catastrophe in Micronesia",
          description = "Aliquip excepteur fugiat ex minim ea aute eu labore. Sunt eiusmod esse eu non commodo est veniam consequat.",
          priority = "low",
          status = "hold",
          submitter_id = 71,
          assignee_id = 38,
          organization_id = 112,
          tags = Set("Puerto Rico", "Idaho", "Oklahoma", "Louisiana"),
          due_at = OffsetDateTime.of(2016, 8, 15, 5, 37, 32, 0, ZoneOffset.ofHours(-10)),
          via = "chat")
      )
    }

    "parse multiple tickets" >> {
      Parser.parseTickets(file("many_tickets")) must beLike[Seq[Ticket]] { case xs =>
        xs.size mustEqual 200
        xs.drop(99).head mustEqual Ticket(
          _id = "ffe688cd-402f-4e37-8597-88b3811bbf46",
          url = "http://initech.zendesk.com/api/v2/tickets/ffe688cd-402f-4e37-8597-88b3811bbf46.json",
          external_id = "a264d753-d2c3-4f50-ba8f-299bf8070f67",
          created_at = OffsetDateTime.of(2016, 2, 3, 5, 47, 0, 0, ZoneOffset.ofHours(-11)),
          `type` = "question",
          subject = "A Problem in Vatican City Ştate (Holy See)",
          description = "Ullamco enim id proident cillum tempor fugiat consequat non enim ad. Consectetur nostrud consequat deserunt consequat sit deserunt cillum esse eu ut fugiat.",
          priority = "urgent",
          status = "open",
          submitter_id = 44,
          assignee_id = 29,
          organization_id = 104,
          tags = Set("District Of Columbia", "Wisconsin", "Illinois", "Fédératéd Statés Of Micronésia"),
          due_at = OffsetDateTime.of(2016, 8, 6, 7, 28, 38, 0, ZoneOffset.ofHours(-10)),
          via = "web"
        )
      }
    }

    "fail to parse a file with non-json content" >> {
      Parser.parseTickets(file("not")) must throwAn[UnparseableFileException]
    }

    "fail to parse a file with incorrect json content" >> {
      Parser.parseTickets(file("single_user")) must throwAn[UnparseableFileException]
    }
  }

  private def file(name: String): File = new File(s"src/test/resources/repo/$name.json")

}