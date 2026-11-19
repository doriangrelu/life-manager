package io.github.doriangrelu.lifemanager.budget.domain.aggregate;

import io.github.doriangrelu.lifemanager.budget.domain.exception.FutureIncomeDateException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Beneficiary;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryName;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeDetails;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Money;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IncomeTest {

    private static final Instant NOW = Instant.parse("2026-11-16T23:30:00Z");
    private static final Clock PARIS = Clock.fixed(NOW, ZoneId.of("Europe/Paris"));
    private static final IncomeId ID = IncomeId.newId();
    private static final HouseholdId HOME = new HouseholdId(UUID.randomUUID());
    private static final MemberId ALICE = new MemberId(UUID.randomUUID());

    @Test
    void recording_keeps_what_was_entered() {
        var income = Income.record(ID, HOME, details(245_000, LocalDate.of(2026, 11, 17)), ALICE, PARIS);

        assertThat(income.details().amount()).isEqualTo(Money.ofCents(245_000));
        assertThat(income.recordedAt()).isEqualTo(NOW);
        assertThat(income.belongsTo(HOME)).isTrue();
    }

    @Test
    void an_income_cannot_be_dated_in_the_future() {
        assertThatThrownBy(() -> Income.record(ID, HOME, details(100, LocalDate.of(2026, 11, 18)), ALICE, PARIS))
                .isInstanceOf(FutureIncomeDateException.class);
    }

    @Test
    void an_income_of_zero_is_impossible() {
        assertThatThrownBy(() -> details(0, LocalDate.of(2026, 11, 1))).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void an_invalid_correction_leaves_the_income_unchanged() {
        var original = details(245_000, LocalDate.of(2026, 11, 10));
        var income = Income.restore(ID, HOME, original, ALICE, NOW);

        assertThatThrownBy(() -> income.correct(details(100, LocalDate.of(2026, 12, 1)), PARIS))
                .isInstanceOf(FutureIncomeDateException.class);
        assertThat(income.details()).isEqualTo(original);
    }

    @Test
    void a_category_only_classifies_its_own_kind() {
        var salary = Category.create(CategoryId.newId(), HOME, new CategoryName("Salaire"), CategoryKind.INCOME);

        assertThat(salary.isUsableFor(HOME, CategoryKind.INCOME)).isTrue();
        assertThat(salary.isUsableFor(HOME, CategoryKind.EXPENSE)).isFalse();
        assertThat(Category.defaultsFor(HOME, CategoryKind.INCOME)).extracting(c -> c.name().value())
                .containsExactly("salaire", "prime", "remboursement", "aides", "autre");
    }

    private static IncomeDetails details(long cents, LocalDate receivedOn) {
        return new IncomeDetails(Money.ofCents(cents), CategoryId.newId(), receivedOn, Beneficiary.SHARED, null);
    }
}
