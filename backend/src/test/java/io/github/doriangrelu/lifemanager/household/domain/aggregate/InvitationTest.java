package io.github.doriangrelu.lifemanager.household.domain.aggregate;

import io.github.doriangrelu.lifemanager.household.domain.event.InvitationAccepted;
import io.github.doriangrelu.lifemanager.household.domain.exception.InvalidInvitationException;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationCode;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.InvitationStatus;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvitationTest {

    private static final InvitationId ID = InvitationId.newId();
    private static final HouseholdId HOUSEHOLD = HouseholdId.newId();
    private static final InvitationCode CODE = new InvitationCode("ABCD2345");
    private static final MemberId BOB = new MemberId(UUID.randomUUID());
    private static final Instant NOW = Instant.parse("2026-11-16T10:00:00Z");

    @Test
    void a_new_invitation_is_usable_for_seven_days() {
        var invitation = Invitation.issue(ID, HOUSEHOLD, CODE, NOW);

        assertThat(invitation.expiresAt()).isEqualTo(NOW.plus(Duration.ofDays(7)));
        assertThat(invitation.isUsable(NOW.plus(Duration.ofDays(6)))).isTrue();
        assertThat(invitation.isUsable(NOW.plus(Duration.ofDays(7)))).isFalse();
    }

    @Test
    void accepting_consumes_the_code() {
        var invitation = Invitation.issue(ID, HOUSEHOLD, CODE, NOW);

        invitation.accept(BOB, NOW);

        assertThat(invitation.status()).isEqualTo(InvitationStatus.ACCEPTED);
        assertThat(invitation.acceptedBy()).contains(BOB);
        assertThat(invitation.pullEvents()).containsExactly(new InvitationAccepted(ID, HOUSEHOLD, BOB, NOW));
        assertThatThrownBy(() -> invitation.accept(BOB, NOW)).isInstanceOf(InvalidInvitationException.class);
    }

    @Test
    void an_expired_invitation_cannot_be_accepted() {
        var invitation = Invitation.issue(ID, HOUSEHOLD, CODE, NOW);

        assertThatThrownBy(() -> invitation.accept(BOB, NOW.plus(Duration.ofDays(8))))
                .isInstanceOf(InvalidInvitationException.class);
    }

    @Test
    void a_revoked_invitation_cannot_be_accepted() {
        var invitation = Invitation.issue(ID, HOUSEHOLD, CODE, NOW);

        invitation.revoke();

        assertThat(invitation.status()).isEqualTo(InvitationStatus.REVOKED);
        assertThatThrownBy(() -> invitation.accept(BOB, NOW)).isInstanceOf(InvalidInvitationException.class);
    }
}
