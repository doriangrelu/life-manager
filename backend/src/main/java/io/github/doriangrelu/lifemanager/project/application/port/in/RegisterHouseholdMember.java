package io.github.doriangrelu.lifemanager.project.application.port.in;

import io.github.doriangrelu.lifemanager.project.application.port.in.command.RegisterHouseholdMemberCommand;
/** Mémorise qu'un utilisateur est membre d'un foyer (copie locale du domaine projets). */
public interface RegisterHouseholdMember {

    void register(RegisterHouseholdMemberCommand command);
}
