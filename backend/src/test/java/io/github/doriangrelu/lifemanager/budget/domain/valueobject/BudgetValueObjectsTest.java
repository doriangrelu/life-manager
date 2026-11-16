package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BudgetValueObjectsTest {

    private static final MemberId ALICE = new MemberId(UUID.randomUUID());

    @Test
    void money_adds_cents_exactly_and_is_never_negative() {
        assertThat(Money.ofCents(1050).plus(Money.ofCents(1999))).isEqualTo(Money.ofCents(3049));
        assertThatThrownBy(() -> Money.ofCents(-1)).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> Money.ofCents(Long.MAX_VALUE).plus(Money.ofCents(1)))
                .isInstanceOf(ArithmeticException.class);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 4250, 100_000_000})
    void an_expense_amount_between_one_cent_and_one_million_euros_is_accepted(long cents) {
        assertThat(details(cents).amount().cents()).isEqualTo(cents);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 100_000_001})
    void an_expense_amount_outside_the_limits_is_refused(long cents) {
        assertThatThrownBy(() -> details(cents)).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void category_name_is_limited_to_40_characters_and_compared_without_case() {
        assertThat(new CategoryName("a".repeat(40)).value()).hasSize(40);
        assertThatThrownBy(() -> new CategoryName("a".repeat(41))).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new CategoryName(" ")).isInstanceOf(InvalidValueException.class);
        assertThat(new CategoryName("Courses").sameAs(new CategoryName("courses"))).isTrue();
    }

    @Test
    void label_is_optional_and_limited_to_140_characters() {
        assertThat(ExpenseLabel.ofNullable("  ")).isNull();
        assertThat(ExpenseLabel.ofNullable(" Plein ")).isEqualTo(new ExpenseLabel("Plein"));
        assertThatThrownBy(() -> new ExpenseLabel("a".repeat(141))).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void no_beneficiary_means_shared() {
        assertThat(Beneficiary.of(null)).isEqualTo(Beneficiary.SHARED);
        assertThat(Beneficiary.of(ALICE.value()).member()).contains(ALICE);
    }

    @Test
    void member_id_must_be_a_uuid() {
        assertThatThrownBy(() -> MemberId.of("pas-un-uuid")).isInstanceOf(InvalidValueException.class);
    }

    private static ExpenseDetails details(long cents) {
        return new ExpenseDetails(Money.ofCents(cents), CategoryId.newId(), LocalDate.of(2026, 11, 16), ALICE,
                Beneficiary.SHARED, null);
    }
}
