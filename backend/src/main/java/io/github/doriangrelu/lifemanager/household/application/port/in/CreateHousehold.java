package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.command.CreateHouseholdCommand;
import io.github.doriangrelu.lifemanager.household.application.port.in.result.HouseholdSummary;
/** Crée un foyer dont le demandeur devient le premier membre. */
public interface CreateHousehold {

    HouseholdSummary create(CreateHouseholdCommand command);
}
