package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.DeleteHousehold;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.DeleteHouseholdCommand;
import io.github.doriangrelu.lifemanager.household.application.port.out.HouseholdRepository;
import io.github.doriangrelu.lifemanager.household.application.port.out.InvitationRepository;
import io.github.doriangrelu.lifemanager.household.application.service.HouseholdAccess;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdName;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.UUID;

/** Supprime le foyer (propriétaire, nom retapé) ; chaque domaine efface ensuite ses données. */
@Singleton
class DeleteHouseholdUseCase implements DeleteHousehold {

    private final HouseholdAccess access;
    private final HouseholdRepository households;
    private final InvitationRepository invitations;
    private final DomainEventPublisher events;
    private final Clock clock;

    DeleteHouseholdUseCase(HouseholdAccess access, HouseholdRepository households,
                               InvitationRepository invitations, DomainEventPublisher events, Clock clock) {
        this.access = access;
        this.households = households;
        this.invitations = invitations;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "household")
    public void delete(DeleteHouseholdCommand command) {
        var household = load(command.householdId(), command.requesterId());
        household.delete(MemberId.of(command.requesterId()), new HouseholdName(command.confirmName()), clock.instant());
        invitations.deleteByHousehold(household.id());
        households.delete(household);
        // Les autres domaines effacent leurs données à réception de l'événement (boîte d'envoi).
        events.publishEventsOf(household);
    }

    private Household load(UUID householdId, String requesterId) {
        return access.loadForMember(new HouseholdId(householdId), MemberId.of(requesterId));
    }
}
