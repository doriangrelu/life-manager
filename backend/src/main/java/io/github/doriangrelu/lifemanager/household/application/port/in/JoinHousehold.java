package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.command.JoinHouseholdCommand;
import io.github.doriangrelu.lifemanager.household.application.port.in.result.HouseholdSummary;
/** Fait entrer le demandeur dans un foyer grâce à un code d'invitation. */
public interface JoinHousehold {

    HouseholdSummary join(JoinHouseholdCommand command);
}
