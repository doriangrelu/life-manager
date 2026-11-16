package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RegisterHouseholdMemberCommand;
/** Mémorise qu'un utilisateur est membre d'un foyer (suite à un événement du domaine foyer). */
public interface RegisterHouseholdMember {

    void register(RegisterHouseholdMemberCommand command);
}
