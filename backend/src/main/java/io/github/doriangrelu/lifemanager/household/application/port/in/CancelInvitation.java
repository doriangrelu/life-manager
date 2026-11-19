package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.command.HouseholdCommand;

/** Annule le code d'invitation actif du foyer. */
public interface CancelInvitation {

    void cancel(HouseholdCommand command);
}
