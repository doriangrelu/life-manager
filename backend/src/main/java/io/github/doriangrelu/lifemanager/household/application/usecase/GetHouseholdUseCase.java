package io.github.doriangrelu.lifemanager.household.application.usecase;

import io.github.doriangrelu.lifemanager.household.application.port.in.GetHousehold;
import io.github.doriangrelu.lifemanager.household.application.port.in.query.GetHouseholdQuery;
import io.github.doriangrelu.lifemanager.household.application.port.in.result.HouseholdDetails;
import io.github.doriangrelu.lifemanager.household.application.port.in.result.InvitationDetails;
import io.github.doriangrelu.lifemanager.household.application.port.in.result.MemberDetails;
import io.github.doriangrelu.lifemanager.household.application.port.out.InvitationRepository;
import io.github.doriangrelu.lifemanager.household.application.port.out.MemberDirectory;
import io.github.doriangrelu.lifemanager.household.application.service.HouseholdAccess;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;
import io.github.doriangrelu.lifemanager.household.domain.entity.Membership;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

/** Détail d'un foyer pour l'un de ses membres : propriétaire, membres nommés, code d'invitation (propriétaire seul). */
@Singleton
class GetHouseholdUseCase implements GetHousehold {

    /** Nom affiché si l'annuaire ne connaît pas encore le membre. */
    static final String UNKNOWN_MEMBER_NAME = "Membre";

    private final HouseholdAccess access;
    private final InvitationRepository invitations;
    private final MemberDirectory directory;
    private final Clock clock;

    GetHouseholdUseCase(HouseholdAccess access, InvitationRepository invitations, MemberDirectory directory,
                        Clock clock) {
        this.access = access;
        this.invitations = invitations;
        this.directory = directory;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "household", readOnly = true)
    public HouseholdDetails get(GetHouseholdQuery query) {
        var requester = MemberId.of(query.requesterId());
        var household = access.loadForMember(new HouseholdId(query.householdId()), requester);
        // Le code d'invitation n'est montré qu'au propriétaire : lui seul invite.
        var invitation = household.isOwner(requester) ? activeInvitation(household).orElse(null) : null;
        return new HouseholdDetails(household.id().value(), household.name().value(), household.owner().value(),
                household.incomeTracking(), namedMembers(household), invitation);
    }

    private List<MemberDetails> namedMembers(Household household) {
        var memberships = household.members();
        var names = directory.displayNamesOf(memberships.stream().map(Membership::memberId).toList());
        return memberships.stream()
                .map(membership -> new MemberDetails(membership.memberId().value(),
                        names.getOrDefault(membership.memberId(), UNKNOWN_MEMBER_NAME),
                        membership.joinedAt()))
                .toList();
    }

    private Optional<InvitationDetails> activeInvitation(Household household) {
        var now = clock.instant();
        return invitations.findPendingByHousehold(household.id())
                .filter(invitation -> invitation.isUsable(now))
                .map(invitation -> new InvitationDetails(invitation.code().value(), invitation.expiresAt()));
    }
}
