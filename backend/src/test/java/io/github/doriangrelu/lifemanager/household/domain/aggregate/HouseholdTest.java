package io.github.doriangrelu.lifemanager.household.domain.aggregate;

import io.github.doriangrelu.lifemanager.household.domain.entity.Membership;
import io.github.doriangrelu.lifemanager.household.domain.event.HouseholdCreated;
import io.github.doriangrelu.lifemanager.household.domain.event.MemberJoined;
import io.github.doriangrelu.lifemanager.household.domain.exception.AlreadyMemberException;
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
    private static final Instant NOW = Instant.parse("2026-11-16T10:00:00Z");

    @Test
    void the_founder_is_the_first_member() {
        var household = Household.create(ID, NAME, ALICE, NOW);

        assertThat(household.members()).extracting(Membership::memberId).containsExactly(ALICE);
        assertThat(household.pullEvents()).containsExactly(new HouseholdCreated(ID, NAME, ALICE, NOW));
    }

    @Test
    void a_new_member_can_join() {
        var household = Household.restore(ID, NAME, NOW, List.of(new Membership(ALICE, NOW)));

        household.addMember(BOB, NOW);

        assertThat(household.isMember(BOB)).isTrue();
        assertThat(household.members()).extracting(Membership::memberId).containsExactly(ALICE, BOB);
        assertThat(household.pullEvents()).containsExactly(new MemberJoined(ID, BOB, NOW));
    }

    @Test
    void a_member_cannot_join_twice() {
        var household = Household.create(ID, NAME, ALICE, NOW);

        assertThatThrownBy(() -> household.addMember(ALICE, NOW)).isInstanceOf(AlreadyMemberException.class);
    }

    @Test
    void a_household_without_member_cannot_exist() {
        assertThatThrownBy(() -> Household.restore(ID, NAME, NOW, List.of()))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void the_name_must_be_valid() {
        assertThatThrownBy(() -> new HouseholdName(" ")).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new HouseholdName("a".repeat(101))).isInstanceOf(InvalidValueException.class);
    }
}
