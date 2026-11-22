package io.github.doriangrelu.lifemanager.project.application.usecase;

import io.github.doriangrelu.lifemanager.project.application.port.in.RegisterHouseholdMember;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.RegisterHouseholdMemberCommand;
import io.github.doriangrelu.lifemanager.project.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
/** Mémorise un membre de foyer dans la copie locale du domaine projets. */
@Singleton
class RegisterHouseholdMemberUseCase implements RegisterHouseholdMember {

    private final HouseholdMembers members;

    RegisterHouseholdMemberUseCase(HouseholdMembers members) {
        this.members = members;
    }

    @Override
    @Transactional(transactionManager = "project")
    public void register(RegisterHouseholdMemberCommand command) {
        members.add(new HouseholdId(command.householdId()), new MemberId(command.memberId()));
    }
}
