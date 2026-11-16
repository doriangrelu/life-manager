package io.github.doriangrelu.lifemanager.budget.domain.aggregate;

import io.github.doriangrelu.lifemanager.budget.domain.event.ExpenseCorrected;
import io.github.doriangrelu.lifemanager.budget.domain.event.ExpenseDeleted;
import io.github.doriangrelu.lifemanager.budget.domain.event.ExpenseRecorded;
import io.github.doriangrelu.lifemanager.budget.domain.exception.FutureExpenseDateException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Beneficiary;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseDetails;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpenseTest {

    // Le 16 novembre 2026 à 23h30 UTC, il est déjà le 17 à Paris.
    private static final Instant NOW = Instant.parse("2026-11-16T23:30:00Z");
    private static final Clock PARIS = Clock.fixed(NOW, ZoneId.of("Europe/Paris"));
    private static final ExpenseId ID = ExpenseId.newId();
    private static final HouseholdId HOME = new HouseholdId(UUID.randomUUID());
    private static final MemberId ALICE = new MemberId(UUID.randomUUID());

    @Test
    void recording_announces_the_new_expense() {
        var expense = Expense.record(ID, HOME, details(4250, LocalDate.of(2026, 11, 17)), ALICE, PARIS);

        assertThat(expense.details().amount()).isEqualTo(Money.ofCents(4250));
        assertThat(expense.recordedAt()).isEqualTo(NOW);
        assertThat(expense.pullEvents()).containsExactly(new ExpenseRecorded(ID, HOME, NOW));
    }

    @Test
    void today_is_the_users_day_not_the_servers() {
        var tomorrowInParis = LocalDate.of(2026, 11, 18);

        assertThatThrownBy(() -> Expense.record(ID, HOME, details(100, tomorrowInParis), ALICE, PARIS))
                .isInstanceOf(FutureExpenseDateException.class);
    }

    @Test
    void correcting_replaces_the_details() {
        var expense = Expense.restore(ID, HOME, details(4250, LocalDate.of(2026, 11, 10)), ALICE, NOW);

        expense.correct(details(2450, LocalDate.of(2026, 11, 10)), PARIS);

        assertThat(expense.details().amount()).isEqualTo(Money.ofCents(2450));
        assertThat(expense.pullEvents()).containsExactly(new ExpenseCorrected(ID, HOME, NOW));
    }

    @Test
    void an_invalid_correction_leaves_the_expense_unchanged() {
        var original = details(4250, LocalDate.of(2026, 11, 10));
        var expense = Expense.restore(ID, HOME, original, ALICE, NOW);

        assertThatThrownBy(() -> expense.correct(details(100, LocalDate.of(2026, 12, 1)), PARIS))
                .isInstanceOf(FutureExpenseDateException.class);
        assertThat(expense.details()).isEqualTo(original);
        assertThat(expense.pullEvents()).isEmpty();
    }

    @Test
    void deleting_is_announced() {
        var expense = Expense.restore(ID, HOME, details(100, LocalDate.of(2026, 11, 10)), ALICE, NOW);

        expense.markDeleted(PARIS);

        assertThat(expense.pullEvents()).containsExactly(new ExpenseDeleted(ID, HOME, NOW));
    }

    private static ExpenseDetails details(long cents, LocalDate spentOn) {
        return new ExpenseDetails(Money.ofCents(cents), CategoryId.newId(), spentOn, ALICE, Beneficiary.SHARED, null);
    }
}
