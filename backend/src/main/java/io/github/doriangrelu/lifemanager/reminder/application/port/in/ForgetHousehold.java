package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.ForgetHouseholdCommand;

/** Efface toutes les données d'un foyer supprimé (événement du domaine foyer). */
public interface ForgetHousehold {

    void forget(ForgetHouseholdCommand command);
}
