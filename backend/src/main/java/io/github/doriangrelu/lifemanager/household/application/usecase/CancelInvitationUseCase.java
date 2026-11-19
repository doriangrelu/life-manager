package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.CancelInvitation;
import io.github.doriangrelu.lifemanager.household.application.port.in.command.HouseholdCommand;
import io.github.doriangrelu.lifemanager.household.application.port.out.InvitationRepository;
import io.github.doriangrelu.lifemanager.household.application.service.HouseholdAccess;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Annule le code d'invitation en attente ; réservé au propriétaire (sans effet s'il n'y en a pas). */
@Singleton
class CancelInvitationUseCase implements CancelInvitation {

    private final HouseholdAccess access;
    private final InvitationRepository invitations;

    CancelInvitationUseCase(HouseholdAccess access, InvitationRepository invitations) {
        this.access = access;
        this.invitations = invitations;
    }

    @Override
    @Transactional(transactionManager = "household")
    public void cancel(HouseholdCommand command) {
        var household = load(command.householdId(), command.requesterId());
        household.requireOwner(MemberId.of(command.requesterId()));
        invitations.findPendingByHousehold(household.id()).ifPresent(invitation -> {
            invitation.revoke();
            invitations.save(invitation);
        });
    }

    private Household load(UUID householdId, String requesterId) {
        return access.loadForMember(new HouseholdId(householdId), MemberId.of(requesterId));
    }
}
