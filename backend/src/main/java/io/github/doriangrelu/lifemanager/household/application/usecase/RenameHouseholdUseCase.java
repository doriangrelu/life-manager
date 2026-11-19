package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.RenameHousehold;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.RenameHouseholdCommand;
import io.github.doriangrelu.lifemanager.household.application.port.out.HouseholdRepository;
import io.github.doriangrelu.lifemanager.household.application.service.HouseholdAccess;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdName;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Renomme le foyer ; réservé au propriétaire. */
@Singleton
class RenameHouseholdUseCase implements RenameHousehold {

    private final HouseholdAccess access;
    private final HouseholdRepository households;

    RenameHouseholdUseCase(HouseholdAccess access, HouseholdRepository households) {
        this.access = access;
        this.households = households;
    }

    @Override
    @Transactional(transactionManager = "household")
    public void rename(RenameHouseholdCommand command) {
        var household = load(command.householdId(), command.requesterId());
        household.rename(MemberId.of(command.requesterId()), new HouseholdName(command.name()));
        households.save(household);
    }

    private Household load(UUID householdId, String requesterId) {
        return access.loadForMember(new HouseholdId(householdId), MemberId.of(requesterId));
    }
}
