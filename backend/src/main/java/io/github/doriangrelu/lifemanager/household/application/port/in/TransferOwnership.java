package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.command.MemberCommand;

/** Transmet la propriété du foyer à un autre membre. */
public interface TransferOwnership {

    void transfer(MemberCommand command);
}
