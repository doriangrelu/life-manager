package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.command.MemberCommand;

/** Retire un membre du foyer (propriétaire seulement). */
public interface RemoveMember {

    void remove(MemberCommand command);
}
