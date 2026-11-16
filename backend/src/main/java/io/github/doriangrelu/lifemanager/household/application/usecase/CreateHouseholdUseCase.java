package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.CreateHousehold;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.CreateHouseholdCommand;
import io.github.doriangrelu.lifemanager.household.application.port.in.result.HouseholdSummary;
import io.github.doriangrelu.lifemanager.household.application.port.out.HouseholdRepository;
import io.github.doriangrelu.lifemanager.household.application.service.MembershipQuota;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdName;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Crée un foyer ; le demandeur en devient le premier membre. */
@Singleton
class CreateHouseholdUseCase implements CreateHousehold {

    private final HouseholdRepository households;
    private final MembershipQuota quota;
    private final DomainEventPublisher events;
    private final Clock clock;

    CreateHouseholdUseCase(HouseholdRepository households, MembershipQuota quota, DomainEventPublisher events,
                           Clock clock) {
        this.households = households;
        this.quota = quota;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "household")
    public HouseholdSummary create(CreateHouseholdCommand command) {
        var founder = MemberId.of(command.requesterId());
        var name = new HouseholdName(command.name());
        quota.ensureCanJoinAnother(founder);

        var household = Household.create(HouseholdId.newId(), name, founder, clock.instant());
        households.save(household);
        events.publishEventsOf(household);
        return HouseholdSummary.from(household);
    }
}
