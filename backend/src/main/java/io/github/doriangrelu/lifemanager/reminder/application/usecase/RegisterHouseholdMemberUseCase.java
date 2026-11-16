package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.RegisterHouseholdMember;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.RegisterHouseholdMemberCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
/** Mémorise un membre de foyer dans la copie locale du domaine rappels. */
@Singleton
class RegisterHouseholdMemberUseCase implements RegisterHouseholdMember {

    private final HouseholdMembers members;

    RegisterHouseholdMemberUseCase(HouseholdMembers members) {
        this.members = members;
    }

    @Override
    @Transactional(transactionManager = "reminder")
    public void register(RegisterHouseholdMemberCommand command) {
        members.add(new HouseholdId(command.householdId()), new MemberId(command.memberId()));
    }
}
