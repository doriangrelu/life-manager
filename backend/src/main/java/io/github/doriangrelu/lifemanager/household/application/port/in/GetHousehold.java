package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.query.GetHouseholdQuery;
import io.github.doriangrelu.lifemanager.household.application.port.in.result.HouseholdDetails;
/** Détail d'un foyer (membres, code d'invitation actif), réservé à ses membres. */
public interface GetHousehold {

    HouseholdDetails get(GetHouseholdQuery query);
}
