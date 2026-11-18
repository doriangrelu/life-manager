package io.github.doriangrelu.lifemanager.bills.application.port.in;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.RegisterHouseholdMemberCommand;

/** Mémorise un membre de foyer (copie locale). */
public interface RegisterHouseholdMember {

    void register(RegisterHouseholdMemberCommand command);
}
