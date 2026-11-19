package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.TransferOwnership;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.MemberCommand;
import io.github.doriangrelu.lifemanager.household.application.port.out.HouseholdRepository;
import io.github.doriangrelu.lifemanager.household.application.service.HouseholdAccess;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Le propriétaire passe la main à un autre membre. */
@Singleton
class TransferOwnershipUseCase implements TransferOwnership {

    private final HouseholdAccess access;
    private final HouseholdRepository households;

    TransferOwnershipUseCase(HouseholdAccess access, HouseholdRepository households) {
        this.access = access;
        this.households = households;
    }

    @Override
    @Transactional(transactionManager = "household")
    public void transfer(MemberCommand command) {
        var household = load(command.householdId(), command.requesterId());
        household.transferOwnership(MemberId.of(command.requesterId()), new MemberId(command.memberId()));
        households.save(household);
    }

    private Household load(UUID householdId, String requesterId) {
        return access.loadForMember(new HouseholdId(householdId), MemberId.of(requesterId));
    }
}
