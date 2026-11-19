package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.ForgetUser;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.ForgetUserCommand;
import io.github.doriangrelu.lifemanager.household.application.port.out.HouseholdRepository;
import io.github.doriangrelu.lifemanager.household.application.port.out.InvitationRepository;
import io.github.doriangrelu.lifemanager.household.application.port.out.MemberDirectory;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.Instant;

/**
 * Réaction à la suppression d'un compte (tâche interne, sans foyer à contrôler) : chaque foyer dont il
 * est seul membre est supprimé, il quitte les autres (transmis au membre arrivé le plus tôt s'il en était
 * propriétaire). Les autres domaines suivent par les événements habituels. Rejouable sans effet.
 */
@Singleton
class ForgetUserUseCase implements ForgetUser {

    private final HouseholdRepository households;
    private final InvitationRepository invitations;
    private final MemberDirectory directory;
    private final DomainEventPublisher events;
    private final Clock clock;

    ForgetUserUseCase(HouseholdRepository households, InvitationRepository invitations, MemberDirectory directory,
                      DomainEventPublisher events, Clock clock) {
        this.households = households;
        this.invitations = invitations;
        this.directory = directory;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "household")
    public void forget(ForgetUserCommand command) {
        var member = new MemberId(command.userId());
        var now = clock.instant();
        for (var household : households.findAllByMember(member)) {
            depart(household, member, now);
            events.publishEventsOf(household);
        }
        directory.forget(member);
    }

    private void depart(Household household, MemberId member, Instant now) {
        if (household.hasOnlyMember(member)) {
            household.delete(member, household.name(), now);
            invitations.deleteByHousehold(household.id());
            households.delete(household);
        } else {
            household.departDeletedAccount(member, now);
            households.save(household);
        }
    }
}
