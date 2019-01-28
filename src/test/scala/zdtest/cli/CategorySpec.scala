package zdtest.cli

import org.scalacheck.Gen
import org.specs2.mutable.Specification
import zdtest.domain._

class CategorySpec extends Specification with ArbitraryInput {

  "category by name" should {
    "parse name 'user'" >> {
      Category.withName("user") must beSome[Category[_]](UserCat)
    }
    "parse name 'organisation'" >> {
      Category.withName("organisation") must beSome[Category[_]](OrgCat)
    }
    "parse name 'organization'" >> {
      Category.withName("organization") must beSome[Category[_]](OrgCat)
    }
    "parse name 'org'" >> {
      Category.withName("org") must beSome[Category[_]](OrgCat)
    }
    "parse name 'ticket'" >> {
      Category.withName("ticket") must beSome[Category[_]](TicketCat)
    }
    "not parse anything else" >> prop { s: String =>
      Category.withName(s) must beNone
    }.setGen(Gen.identifier.suchThat(s =>
      !Set("user", "organisation", "organization", "org", "ticket").contains(s)
    ))
  }

  "category fields" should {
    "extract the correct values for users" >> prop { u: User =>
      UserCat.fields.get("_id").map(_(u)) must beSome(u._id.toString)
      UserCat.fields.get("url").map(_(u)) must beSome(u.url)
      UserCat.fields.get("external_id").map(_(u)) must beSome(u.external_id)
      UserCat.fields.get("name").map(_(u)) must beSome(u.name)
      UserCat.fields.get("alias").map(_(u)) must beSome(u.alias)
      UserCat.fields.get("created_at").map(_(u)) must beSome(DateFormat.formatter.format(u.created_at))
      UserCat.fields.get("active").map(_(u)) must beSome(u.active.toString)
      UserCat.fields.get("verified").map(_(u)) must beSome(u.verified.toString)
      UserCat.fields.get("shared").map(_(u)) must beSome(u.shared.toString)
      UserCat.fields.get("locale").map(_(u)) must beSome(u.locale)
      UserCat.fields.get("timezone").map(_(u)) must beSome(u.timezone)
      UserCat.fields.get("last_login_at").map(_(u)) must beSome(DateFormat.formatter.format(u.last_login_at))
      UserCat.fields.get("email").map(_(u)) must beSome(u.email)
      UserCat.fields.get("phone").map(_(u)) must beSome(u.phone)
      UserCat.fields.get("signature").map(_(u)) must beSome(u.signature)
      UserCat.fields.get("organization_id").map(_(u)) must beSome(u.organization_id.toString)
      UserCat.fields.get("tags").map(_(u)) must beSome(u.tags.mkString(" "))
      UserCat.fields.get("suspended").map(_(u)) must beSome(u.suspended.toString)
      UserCat.fields.get("role").map(_(u)) must beSome(u.role)
    }
  }

  "extract the correct values for orgs" >> prop { o: Organisation =>
    OrgCat.fields.get("_id").map(_(o)) must beSome(o._id.toString)
    OrgCat.fields.get("url").map(_(o)) must beSome(o.url)
    OrgCat.fields.get("external_id").map(_(o)) must beSome(o.external_id)
    OrgCat.fields.get("name").map(_(o)) must beSome(o.name)
    OrgCat.fields.get("domain_names").map(_(o)) must beSome(o.domain_names.mkString(" "))
    OrgCat.fields.get("created_at").map(_(o)) must beSome(DateFormat.formatter.format(o.created_at))
    OrgCat.fields.get("details").map(_(o)) must beSome(o.details)
    OrgCat.fields.get("shared_tickets").map(_(o)) must beSome(o.shared_tickets.toString)
    OrgCat.fields.get("tags").map(_(o)) must beSome(o.tags.mkString(" "))
  }

  "extract the correct values for tickets" >> prop { t: Ticket =>
    TicketCat.fields.get("_id").map(_(t)) must beSome(t._id.toString)
    TicketCat.fields.get("url").map(_(t)) must beSome(t.url)
    TicketCat.fields.get("external_id").map(_(t)) must beSome(t.external_id)
    TicketCat.fields.get("created_at").map(_(t)) must beSome(DateFormat.formatter.format(t.created_at))
    TicketCat.fields.get("type").map(_(t)) must beSome(t.`type`)
    TicketCat.fields.get("subject").map(_(t)) must beSome(t.subject)
    TicketCat.fields.get("description").map(_(t)) must beSome(t.description)
    TicketCat.fields.get("priority").map(_(t)) must beSome(t.priority)
    TicketCat.fields.get("status").map(_(t)) must beSome(t.status)
    TicketCat.fields.get("submitter_id").map(_(t)) must beSome(t.submitter_id.toString)
    TicketCat.fields.get("assignee_id").map(_(t)) must beSome(t.assignee_id.toString)
    TicketCat.fields.get("organization_id").map(_(t)) must beSome(t.organization_id.toString)
    TicketCat.fields.get("tags").map(_(t)) must beSome(t.tags.mkString(" "))
    TicketCat.fields.get("has_incidents").map(_(t)) must beSome(t.has_incidents.toString)
    TicketCat.fields.get("due_at").map(_(t)) must beSome(DateFormat.formatter.format(t.due_at))
    TicketCat.fields.get("via").map(_(t)) must beSome(t.via)
  }
}
