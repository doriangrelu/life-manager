package io.github.doriangrelu.lifemanager.household.domain.aggregate;

import io.github.doriangrelu.lifemanager.household.domain.entity.Membership;
import io.github.doriangrelu.lifemanager.household.domain.event.HouseholdCreated;
import io.github.doriangrelu.lifemanager.household.domain.event.HouseholdDeleted;
import io.github.doriangrelu.lifemanager.household.domain.event.MemberJoined;
import io.github.doriangrelu.lifemanager.household.domain.event.MemberLeft;
import io.github.doriangrelu.lifemanager.household.domain.exception.AlreadyMemberException;
import io.github.doriangrelu.lifemanager.household.domain.exception.HouseholdNameMismatchException;
import io.github.doriangrelu.lifemanager.household.domain.exception.MemberNotFoundException;
import io.github.doriangrelu.lifemanager.household.domain.exception.NotOwnerException;
import io.github.doriangrelu.lifemanager.household.domain.exception.OwnerCannotLeaveException;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdName;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HouseholdTest {

    private static final HouseholdId ID = HouseholdId.newId();
    private static final HouseholdName NAME = new HouseholdName("Maison");
    private static final MemberId ALICE = new MemberId(UUID.randomUUID());
    private static final MemberId BOB = new MemberId(UUID.randomUUID());
    private static final MemberId CAROL = new MemberId(UUID.randomUUID());
    private static final Instant NOW = Instant.parse("2026-11-16T10:00:00Z");

    @Test
    void the_founder_is_the_first_member_and_the_owner() {
        var household = Household.create(ID, NAME, ALICE, NOW);

        assertThat(household.members()).extracting(Membership::memberId).containsExactly(ALICE);
        assertThat(household.isOwner(ALICE)).isTrue();
        assertThat(household.pullEvents()).containsExactly(new HouseholdCreated(ID, NAME, ALICE, NOW));
    }

    @Test
    void a_new_member_can_join() {
        var household = aliceAndBob();

        household.addMember(CAROL, NOW);

        assertThat(household.members()).extracting(Membership::memberId).containsExactly(ALICE, BOB, CAROL);
        assertThat(household.pullEvents()).containsExactly(new MemberJoined(ID, CAROL, NOW));
    }

    @Test
    void a_member_cannot_join_twice() {
        var household = Household.create(ID, NAME, ALICE, NOW);

        assertThatThrownBy(() -> household.addMember(ALICE, NOW)).isInstanceOf(AlreadyMemberException.class);
    }

    @Test
    void a_household_needs_members_and_an_owner_among_them() {
        assertThatThrownBy(() -> Household.restore(ID, NAME, NOW, ALICE, List.of()))
                .isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> Household.restore(ID, NAME, NOW, BOB, List.of(new Membership(ALICE, NOW))))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void the_owner_can_hand_over_the_household() {
        var household = aliceAndBob();

        household.transferOwnership(ALICE, BOB);

        assertThat(household.isOwner(BOB)).isTrue();
        assertThatThrownBy(() -> household.transferOwnership(ALICE, ALICE)).isInstanceOf(NotOwnerException.class);
        assertThatThrownBy(() -> household.transferOwnership(BOB, CAROL)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void only_the_owner_removes_a_member_but_never_himself() {
        var household = aliceAndBob();

        assertThatThrownBy(() -> household.removeMember(BOB, ALICE, NOW)).isInstanceOf(NotOwnerException.class);
        assertThatThrownBy(() -> household.removeMember(ALICE, ALICE, NOW))
                .isInstanceOf(OwnerCannotLeaveException.class);
        household.removeMember(ALICE, BOB, NOW);

        assertThat(household.isMember(BOB)).isFalse();
        assertThat(household.pullEvents()).containsExactly(new MemberLeft(ID, BOB, ALICE, NOW));
    }

    @Test
    void a_member_can_leave_but_not_the_owner() {
        var household = aliceAndBob();

        assertThatThrownBy(() -> household.leave(ALICE, NOW)).isInstanceOf(OwnerCannotLeaveException.class);
        household.leave(BOB, NOW);

        assertThat(household.members()).extracting(Membership::memberId).containsExactly(ALICE);
        assertThat(household.pullEvents()).containsExactly(new MemberLeft(ID, BOB, ALICE, NOW));
    }

    @Test
    void only_the_owner_renames_or_deletes_with_the_exact_name() {
        var household = aliceAndBob();

        assertThatThrownBy(() -> household.rename(BOB, new HouseholdName("Chez Bob")))
                .isInstanceOf(NotOwnerException.class);
        household.rename(ALICE, new HouseholdName("Maison Grelu"));
        assertThat(household.name().value()).isEqualTo("maison grelu");

        assertThatThrownBy(() -> household.delete(ALICE, new HouseholdName("Maison"), NOW))
                .isInstanceOf(HouseholdNameMismatchException.class);
        assertThatThrownBy(() -> household.delete(BOB, new HouseholdName("Maison Grelu"), NOW))
                .isInstanceOf(NotOwnerException.class);
        household.delete(ALICE, new HouseholdName("  MAISON grelu "), NOW);
        assertThat(household.pullEvents()).containsExactly(new HouseholdDeleted(ID, NOW));
    }

    @Test
    void the_name_must_be_valid() {
        assertThatThrownBy(() -> new HouseholdName(" ")).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new HouseholdName("a".repeat(101))).isInstanceOf(InvalidValueException.class);
    }

    private static Household aliceAndBob() {
        return Household.restore(ID, NAME, NOW, ALICE, List.of(new Membership(ALICE, NOW), new Membership(BOB, NOW)));
    }
}
