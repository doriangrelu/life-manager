package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.RegisterHouseholdMemberCommand;
/** Mémorise qu'un utilisateur est membre d'un foyer (copie locale du domaine rappels). */
public interface RegisterHouseholdMember {

    void register(RegisterHouseholdMemberCommand command);
}
