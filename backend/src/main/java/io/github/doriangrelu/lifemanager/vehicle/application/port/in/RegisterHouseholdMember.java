package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.RegisterHouseholdMemberCommand;
/** Mémorise qu'un utilisateur est membre d'un foyer (copie locale du domaine véhicule). */
public interface RegisterHouseholdMember {

    void register(RegisterHouseholdMemberCommand command);
}
