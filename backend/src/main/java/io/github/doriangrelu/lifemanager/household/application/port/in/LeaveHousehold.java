package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.command.HouseholdCommand;

/** Le demandeur quitte un foyer dont il n'est pas propriétaire. */
public interface LeaveHousehold {

    void leave(HouseholdCommand command);
}
