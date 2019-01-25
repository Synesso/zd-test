package zdtest.repo

import zdtest.domain.Organisation

case class Repository(organisations: Map[Long, Organisation] = Map.empty) {

  def withOrganisations(orgs: Seq[Organisation]): Repository = copy(
    organisations = orgs.foldLeft(organisations) { case (acc, org) =>
        acc.updated(org._id, org)
    }
  )

}
