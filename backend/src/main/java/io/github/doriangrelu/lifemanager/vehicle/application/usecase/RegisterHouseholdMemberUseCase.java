package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.RegisterHouseholdMember;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.RegisterHouseholdMemberCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
/** Mémorise un membre de foyer dans la copie locale du domaine véhicule. */
@Singleton
class RegisterHouseholdMemberUseCase implements RegisterHouseholdMember {

    private final HouseholdMembers members;

    RegisterHouseholdMemberUseCase(HouseholdMembers members) {
        this.members = members;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public void register(RegisterHouseholdMemberCommand command) {
        members.add(new HouseholdId(command.householdId()), new MemberId(command.memberId()));
    }
}
