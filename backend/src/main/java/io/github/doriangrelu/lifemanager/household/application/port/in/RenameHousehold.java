package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.command.RenameHouseholdCommand;

/** Renomme un foyer (propriétaire seulement). */
public interface RenameHousehold {

    void rename(RenameHouseholdCommand command);
}
