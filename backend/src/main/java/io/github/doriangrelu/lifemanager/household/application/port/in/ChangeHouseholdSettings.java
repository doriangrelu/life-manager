package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.command.HouseholdSettingsCommand;

/** Change les réglages d'un foyer (propriétaire seulement). */
public interface ChangeHouseholdSettings {

    void change(HouseholdSettingsCommand command);
}
