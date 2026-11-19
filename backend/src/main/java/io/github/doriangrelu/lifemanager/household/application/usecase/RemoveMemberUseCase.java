package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.RemoveMember;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.MemberCommand;
import io.github.doriangrelu.lifemanager.household.application.port.out.HouseholdRepository;
import io.github.doriangrelu.lifemanager.household.application.service.HouseholdAccess;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.UUID;

/** Le propriétaire retire un membre : les autres domaines lui coupent l'accès (événement). */
@Singleton
class RemoveMemberUseCase implements RemoveMember {

    private final HouseholdAccess access;
    private final HouseholdRepository households;
    private final DomainEventPublisher events;
    private final Clock clock;

    RemoveMemberUseCase(HouseholdAccess access, HouseholdRepository households,
                            DomainEventPublisher events, Clock clock) {
        this.access = access;
        this.households = households;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "household")
    public void remove(MemberCommand command) {
        var household = load(command.householdId(), command.requesterId());
        household.removeMember(MemberId.of(command.requesterId()), new MemberId(command.memberId()), clock.instant());
        households.save(household);
        events.publishEventsOf(household);
    }

    private Household load(UUID householdId, String requesterId) {
        return access.loadForMember(new HouseholdId(householdId), MemberId.of(requesterId));
    }
}
