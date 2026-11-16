package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.IssueInvitation;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.IssueInvitationCommand;
import io.github.doriangrelu.lifemanager.household.application.port.in.result.InvitationDetails;
import io.github.doriangrelu.lifemanager.household.application.port.out.InvitationRepository;
import io.github.doriangrelu.lifemanager.household.application.service.HouseholdAccess;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Invitation;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationCode;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.random.RandomGenerator;

/** Génère un nouveau code d'invitation ; le code précédent encore en attente est annulé. */
@Singleton
class IssueInvitationUseCase implements IssueInvitation {

    // Générateur cryptographique : un code ne doit pas être prévisible.
    private final RandomGenerator random = new SecureRandom();

    private final HouseholdAccess access;
    private final InvitationRepository invitations;
    private final Clock clock;

    IssueInvitationUseCase(HouseholdAccess access, InvitationRepository invitations, Clock clock) {
        this.access = access;
        this.invitations = invitations;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "household")
    public InvitationDetails issue(IssueInvitationCommand command) {
        var household = access.loadForMember(new HouseholdId(command.householdId()), MemberId.of(command.requesterId()));

        invitations.findPendingByHousehold(household.id()).ifPresent(previous -> {
            previous.revoke();
            invitations.save(previous);
        });

        var invitation = Invitation.issue(InvitationId.newId(), household.id(), InvitationCode.random(random),
                clock.instant());
        invitations.save(invitation);
        return new InvitationDetails(invitation.code().value(), invitation.expiresAt());
    }
}
