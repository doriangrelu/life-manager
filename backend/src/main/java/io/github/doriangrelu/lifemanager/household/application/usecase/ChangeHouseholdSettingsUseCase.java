package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.ChangeHouseholdSettings;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.HouseholdSettingsCommand;
import io.github.doriangrelu.lifemanager.household.application.port.out.HouseholdRepository;
import io.github.doriangrelu.lifemanager.household.application.service.HouseholdAccess;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Le propriétaire change les réglages du foyer (suivi des rentrées). */
@Singleton
class ChangeHouseholdSettingsUseCase implements ChangeHouseholdSettings {

    private final HouseholdAccess access;
    private final HouseholdRepository households;

    ChangeHouseholdSettingsUseCase(HouseholdAccess access, HouseholdRepository households) {
        this.access = access;
        this.households = households;
    }

    @Override
    @Transactional(transactionManager = "household")
    public void change(HouseholdSettingsCommand command) {
        var requester = MemberId.of(command.requesterId());
        var household = access.loadForMember(new HouseholdId(command.householdId()), requester);
        household.trackIncome(requester, command.incomeTracking());
        households.save(household);
    }
}
