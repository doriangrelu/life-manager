package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.JoinHousehold;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.JoinHouseholdCommand;
import io.github.doriangrelu.lifemanager.household.application.port.in.result.HouseholdSummary;
import io.github.doriangrelu.lifemanager.household.application.port.out.HouseholdRepository;
import io.github.doriangrelu.lifemanager.household.application.port.out.InvitationRepository;
import io.github.doriangrelu.lifemanager.household.application.service.MembershipQuota;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Invitation;
import io.github.doriangrelu.lifemanager.household.domain.exception.AlreadyMemberException;
import io.github.doriangrelu.lifemanager.household.domain.exception.InvalidInvitationException;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationCode;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.Instant;

/**
 * Fait entrer le demandeur dans un foyer grâce à un code d'invitation.
 * Modifie deux agrégats (invitation consommée + nouveau membre) dans une même transaction :
 * exception assumée et documentée dans le design, l'un n'a pas de sens sans l'autre.
 */
@Singleton
class JoinHouseholdUseCase implements JoinHousehold {

    private final InvitationRepository invitations;
    private final HouseholdRepository households;
    private final MembershipQuota quota;
    private final DomainEventPublisher events;
    private final Clock clock;

    JoinHouseholdUseCase(InvitationRepository invitations, HouseholdRepository households, MembershipQuota quota,
                         DomainEventPublisher events, Clock clock) {
        this.invitations = invitations;
        this.households = households;
        this.quota = quota;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "household")
    public HouseholdSummary join(JoinHouseholdCommand command) {
        var member = MemberId.of(command.requesterId());
        var now = clock.instant();
        var invitation = usableInvitation(new InvitationCode(command.code()), now);
        var household = households.findById(invitation.householdId()).orElseThrow(InvalidInvitationException::new);

        // Vérifié avant de consommer le code : en cas de refus, le code reste utilisable.
        if (household.isMember(member)) {
            throw new AlreadyMemberException();
        }
        quota.ensureCanJoinAnother(member);

        invitation.accept(member, now);
        household.addMember(member, now);
        save(invitation, household);
        return HouseholdSummary.from(household);
    }

    private Invitation usableInvitation(InvitationCode code, Instant now) {
        return invitations.findByCode(code)
                .filter(invitation -> invitation.isUsable(now))
                .orElseThrow(InvalidInvitationException::new);
    }

    private void save(Invitation invitation, Household household) {
        invitations.save(invitation);
        households.save(household);
        events.publishEventsOf(invitation);
        events.publishEventsOf(household);
    }
}
