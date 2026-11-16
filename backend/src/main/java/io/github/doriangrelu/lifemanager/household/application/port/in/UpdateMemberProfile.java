package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.command.UpdateMemberProfileCommand;
/** Met à jour le nom d'un membre dans l'annuaire local (suite à un événement du domaine identité). */
public interface UpdateMemberProfile {

    void update(UpdateMemberProfileCommand command);
}
