package zdtest.repo

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Files
import java.time.{OffsetDateTime, ZoneOffset}

import org.specs2.mutable.Specification
import zdtest.domain.{ArbitraryInput, Organisation}
import upickle.default._

class ParserSpec extends Specification with ArbitraryInput {

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

    // Requires about 500MB of disk space.
    "parse vast quantities of organisations" >> {
      val f = mkTempFile
      val json = write[Organisation](genOrg.sample.get)
      val writer = new BufferedWriter(new FileWriter(f))
      writer.write("[")
      Stream.continually(json + ",").take(99999).foreach(writer.write)
      writer.write(json)
      writer.write("]")
      writer.close()

      val organisations = read[Stream[Organisation]](f)
      organisations must haveSize(100000)
    }
  }



  private def file(name: String): File = new File(s"src/test/resources/repo/$name.json")

  private def mkTempFile: File = {
    val f = Files.createTempFile("zdtest", "").toFile
    f.deleteOnExit()
    f
  }

}