package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.ForgetHouseholdMemberCommand;

/** Coupe l'accès d'un membre parti du foyer (événement du domaine foyer). */
public interface ForgetHouseholdMember {

    void forget(ForgetHouseholdMemberCommand command);
}
