package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.command.DeleteHouseholdCommand;

/** Supprime définitivement un foyer et toutes ses données. */
public interface DeleteHousehold {

    void delete(DeleteHouseholdCommand command);
}
