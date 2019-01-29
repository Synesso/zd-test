package zdtest.search

import java.time.OffsetDateTime

import org.scalacheck.Gen
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import zdtest.domain._

import scala.concurrent.Await
import scala.concurrent.duration._

class IndexSpec(implicit ee: ExecutionEnv) extends Specification with ArbitraryInput {

  sequential

  "an index" should {
    "be allowed to be empty" >> {
      val index = Await.result(Index.build(), 5.seconds)
      index.search(UserCat, "_id", "1") mustEqual Nil
    }

    "allow multiple entries on the same key" >> {
      val orgA = genOrg.sample.get
      val orgB = orgA.copy(_id = orgA._id + 1)
      val index = Await.result(Index.build(organisations = Seq(orgA, orgB)), 5.seconds)
      index.search(OrgCat, "name", orgA.name) must containTheSameElementsAs(Seq(orgA, orgB))
    }

    "allow case-insensitive searching on all fields on organisations" >> {
      val xs = Gen.nonEmptyListOf(genOrg).sample.get
      val index = Await.result(Index.build(organisations = xs), 5.seconds)
      val variants = for {
        x <- xs
        (key, value) <- OrgCat.fields.mapValues(_ (x))
        prefix = value.take(2).takeWhile(c => !Character.isWhitespace(c)).toUpperCase
      } yield (x, key, value, prefix)
      forall(variants) { case (org, key, value, prefix) =>
        val i = index.search(OrgCat, key, prefix)
        i must contain[Searchable](org).unless(value == "")
      }
    }

    "allow case-insensitive searching on all fields on users" >> {
      val xs = Gen.nonEmptyListOf(genUser).sample.get
      val index = Await.result(Index.build(users = xs), 5.seconds)
      val variants = for {
        x <- xs
        (key, value) <- UserCat.fields.mapValues(_ (x))
        prefix = value.take(2).takeWhile(c => !Character.isWhitespace(c)).toUpperCase
      } yield (x, key, value, prefix)
      forall(variants) { case (org, key, value, prefix) =>
        val i = index.search(UserCat, key, prefix)
        i must contain[Searchable](org).unless(value == "")
      }
    }

    "allow case-insensitive searching on all fields on tickets" >> {
      val xs = Gen.nonEmptyListOf(genTicket).sample.get
      val index = Await.result(Index.build(tickets = xs), 5.seconds)
      val variants = for {
        x <- xs
        (key, value) <- TicketCat.fields.mapValues(_ (x))
        prefix = value.take(2).takeWhile(c => !Character.isWhitespace(c)).toUpperCase
      } yield (x, key, value, prefix)
      forall(variants) { case (org, key, value, prefix) =>
        val i = index.search(TicketCat, key, prefix)
        i must contain[Searchable](org).unless(value == "")
      }
    }

    "allow searching for empty string on all fields on orgs" >> {
      val org = Organisation(1, created_at = OffsetDateTime.MIN)
      val index = Await.result(Index.build(organisations = Seq(org)), 5.seconds)
      val keys = OrgCat.fields.mapValues(_(org)).filter(_._2 == "").keys // only fields where the value is an empty string
      forall(keys) { k: String => index.search(OrgCat, k, "") mustEqual Seq(org) }
    }

    "allow searching for empty string on all fields on users" >> {
      val user = User(1, created_at = OffsetDateTime.MIN, last_login_at = OffsetDateTime.MIN)
      val index = Await.result(Index.build(users = Seq(user)), 5.seconds)
      val keys = UserCat.fields.mapValues(_(user)).filter(_._2 == "").keys // only fields where the value is an empty string
      forall(keys) { k: String => index.search(UserCat, k, "") mustEqual Seq(user) }
    }

    "allow searching for empty string on all fields on tickets" >> {
      val ticket = Ticket("", created_at = OffsetDateTime.MIN)
      val index = Await.result(Index.build(tickets = Seq(ticket)), 5.seconds)
      val keys = TicketCat.fields.mapValues(_(ticket)).filter(_._2 == "").keys // only fields where the value is an empty string
      forall(keys) { k: String => index.search(TicketCat, k, "") mustEqual Seq(ticket) }
    }
  }

}
