package io.github.doriangrelu.lifemanager.budget.domain.aggregate;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.BudgetScope;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.PlannedAmount;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryBudgetTest {

    private static final HouseholdId HOUSEHOLD = new HouseholdId(UUID.randomUUID());
    private static final CategoryId GROCERIES = CategoryId.newId();
    private static final YearMonth NOVEMBER = YearMonth.of(2026, 11);

    @Test
    void a_rule_from_a_month_applies_to_that_month_and_the_following_ones() {
        var rules = List.of(from(NOVEMBER, 40_000L));

        assertThat(planned(rules, NOVEMBER.minusMonths(1))).isEmpty();
        assertThat(planned(rules, NOVEMBER)).containsExactly(40_000L);
        assertThat(planned(rules, NOVEMBER.plusMonths(14))).containsExactly(40_000L);
    }

    @Test
    void a_single_month_rule_wins_for_its_month_only() {
        var rules = List.of(from(NOVEMBER, 40_000L), only(NOVEMBER.plusMonths(1), 60_000L));

        assertThat(planned(rules, NOVEMBER)).containsExactly(40_000L);
        assertThat(planned(rules, NOVEMBER.plusMonths(1))).containsExactly(60_000L);
        assertThat(planned(rules, NOVEMBER.plusMonths(2))).containsExactly(40_000L);
    }

    @Test
    void a_newer_rule_does_not_change_past_months() {
        var rules = List.of(from(NOVEMBER.plusMonths(2), 45_000L), from(NOVEMBER, 40_000L));

        assertThat(planned(rules, NOVEMBER.plusMonths(1))).containsExactly(40_000L);
        assertThat(planned(rules, NOVEMBER.plusMonths(2))).containsExactly(45_000L);
    }

    @Test
    void a_rule_without_amount_removes_the_plan() {
        var rules = List.of(from(NOVEMBER, 40_000L), from(NOVEMBER.plusMonths(4), null),
                only(NOVEMBER.plusMonths(1), null));

        assertThat(planned(rules, NOVEMBER)).containsExactly(40_000L);
        assertThat(planned(rules, NOVEMBER.plusMonths(1))).isEmpty();
        assertThat(planned(rules, NOVEMBER.plusMonths(4))).isEmpty();
    }

    @Test
    void each_category_has_its_own_plan() {
        var leisure = CategoryBudget.set(HOUSEHOLD, CategoryId.newId(),
                new CategoryBudget.Period(NOVEMBER, BudgetScope.FROM_MONTH), new PlannedAmount(15_000L));

        assertThat(planned(List.of(from(NOVEMBER, 40_000L), leisure), NOVEMBER))
                .containsExactlyInAnyOrder(40_000L, 15_000L);
    }

    @Test
    void a_planned_amount_stays_within_bounds() {
        assertThat(PlannedAmount.ofNullable(null)).isNull();
        assertThat(new PlannedAmount(100_000_000L).cents()).isEqualTo(100_000_000L);
        assertThatThrownBy(() -> new PlannedAmount(0)).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new PlannedAmount(100_000_001L)).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> BudgetScope.of("ALWAYS")).isInstanceOf(InvalidValueException.class);
    }

    private static List<Long> planned(List<CategoryBudget> rules, YearMonth month) {
        return CategoryBudget.applicableIn(rules, month).stream()
                .map(rule -> rule.planned().orElseThrow().cents())
                .toList();
    }

    private static CategoryBudget from(YearMonth month, Long cents) {
        return rule(month, BudgetScope.FROM_MONTH, cents);
    }

    private static CategoryBudget only(YearMonth month, Long cents) {
        return rule(month, BudgetScope.MONTH_ONLY, cents);
    }

    private static CategoryBudget rule(YearMonth month, BudgetScope scope, Long cents) {
        return CategoryBudget.set(HOUSEHOLD, GROCERIES, new CategoryBudget.Period(month, scope),
                PlannedAmount.ofNullable(cents));
    }
}
