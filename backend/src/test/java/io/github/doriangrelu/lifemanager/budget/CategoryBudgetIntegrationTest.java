package io.github.doriangrelu.lifemanager.budget;

import io.github.doriangrelu.lifemanager.budget.BudgetTestUsers.TestUser;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.BudgetReportResponse.MonthAmount;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.CategoryBudgetRequest;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.ExpenseRequest;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.MonthlyBudgetResponse.Plan;
import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.budget.BudgetTestUsers.newUser;
import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

/** Budget prévu par catégorie : règles datées, relues dans la vue du mois. */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryBudgetIntegrationTest extends IntegrationTestSupport {

    private static final String FROM_MONTH = "FROM_MONTH";
    private static final String MONTH_ONLY = "MONTH_ONLY";

    @Inject
    @Client("/")
    HttpClient client;

    HouseholdApi households;
    BudgetApi budget;
    TestUser alice;
    UUID household;
    UUID groceries;

    @BeforeEach
    void newHousehold() {
        households = new HouseholdApi(client.toBlocking());
        budget = new BudgetApi(client.toBlocking());
        alice = newUser(KEYCLOAK, "Alice");
        household = households.create(alice.token(), "Maison").id();
        groceries = budget.category(alice.token(), household, "Courses");
    }

    @Test
    void a_plan_applies_from_its_month_and_leaves_the_past_untouched() {
        set(groceries, "2026-11", FROM_MONTH, 40_000L);
        set(groceries, "2027-01", FROM_MONTH, 45_000L);

        assertThat(plans("2026-10")).isEmpty();
        assertThat(plans("2026-11")).containsExactly(new Plan(groceries, 40_000, FROM_MONTH));
        assertThat(plans("2026-12")).containsExactly(new Plan(groceries, 40_000, FROM_MONTH));
        assertThat(plans("2027-03")).containsExactly(new Plan(groceries, 45_000, FROM_MONTH));
    }

    @Test
    void a_single_month_can_be_adjusted_then_aligned_again() {
        set(groceries, "2026-11", FROM_MONTH, 40_000L);
        set(groceries, "2026-12", MONTH_ONLY, 60_000L);

        assertThat(plans("2026-12")).containsExactly(new Plan(groceries, 60_000, MONTH_ONLY));
        assertThat(plans("2027-01")).containsExactly(new Plan(groceries, 40_000, FROM_MONTH));

        // « Tous les mois » à partir de décembre efface l'ajustement de décembre.
        set(groceries, "2026-12", FROM_MONTH, 42_000L);

        assertThat(plans("2026-12")).containsExactly(new Plan(groceries, 42_000, FROM_MONTH));
        assertThat(plans("2026-11")).containsExactly(new Plan(groceries, 40_000, FROM_MONTH));
    }

    @Test
    void a_plan_can_be_changed_and_removed() {
        set(groceries, "2026-11", FROM_MONTH, 40_000L);
        set(groceries, "2026-11", FROM_MONTH, 38_000L);
        set(groceries, "2026-12", MONTH_ONLY, null);
        set(groceries, "2027-03", FROM_MONTH, null);

        assertThat(plans("2026-11")).containsExactly(new Plan(groceries, 38_000, FROM_MONTH));
        assertThat(plans("2026-12")).isEmpty();
        assertThat(plans("2027-02")).containsExactly(new Plan(groceries, 38_000, FROM_MONTH));
        assertThat(plans("2027-03")).isEmpty();
    }

    @Test
    void a_planned_category_is_listed_even_without_expense_and_ordinary_expenses_are_not_recurring() {
        var leisure = budget.category(alice.token(), household, "Loisirs");
        set(leisure, "2026-10", FROM_MONTH, 15_000L);
        budget.record(alice.token(), household,
                new ExpenseRequest(3000L, groceries, null, LocalDate.of(2026, 10, 3), alice.id(), null, null));

        var month = budget.month(alice.token(), household, "2026-10");

        assertThat(month.plans()).containsExactly(new Plan(leisure, 15_000, FROM_MONTH));
        assertThat(month.totalCents()).isEqualTo(3000);
        assertThat(month.recurringExpenseCents()).isZero();
    }

    @Test
    void the_report_gives_plans_and_expenses_month_by_month() {
        set(groceries, "2026-10", FROM_MONTH, 40_000L);
        set(groceries, "2026-11", MONTH_ONLY, 60_000L);
        budget.record(alice.token(), household,
                new ExpenseRequest(38_000L, groceries, null, LocalDate.of(2026, 10, 3), alice.id(), null, null));
        budget.record(alice.token(), household,
                new ExpenseRequest(45_000L, groceries, null, LocalDate.of(2026, 11, 3), alice.id(), null, null));

        var report = budget.report(alice.token(), household, "2026-09", "2026-11");

        assertThat(report.monthPlans()).containsExactly(
                new MonthAmount("2026-10", groceries, 40_000), new MonthAmount("2026-11", groceries, 60_000));
        assertThat(report.monthExpenses()).containsExactly(
                new MonthAmount("2026-10", groceries, 38_000), new MonthAmount("2026-11", groceries, 45_000));
    }

    @Test
    void invalid_plans_are_refused() {
        var salary = budget.incomeCategory(alice.token(), household, "Salaire");
        var archived = budget.category(alice.token(), household, "Santé");
        budget.archive(alice.token(), household, archived);

        assertThat(code(groceries, "2026-11", FROM_MONTH, 0L)).isEqualTo(400);
        assertThat(code(groceries, "2026-11", FROM_MONTH, 100_000_001L)).isEqualTo(400);
        assertThat(code(groceries, "2026-13", FROM_MONTH, 40_000L)).isEqualTo(400);
        assertThat(code(groceries, "2026-11", "ALWAYS", 40_000L)).isEqualTo(400);
        assertThat(code(archived, "2026-11", FROM_MONTH, 40_000L)).isEqualTo(400);
        assertThat(code(salary, "2026-11", FROM_MONTH, 40_000L)).isEqualTo(404);
        assertThat(code(UUID.randomUUID(), "2026-11", FROM_MONTH, 40_000L)).isEqualTo(404);
    }

    @Test
    void a_stranger_cannot_plan_for_another_household() {
        var stranger = newUser(KEYCLOAK, "Zoé");
        var request = new CategoryBudgetRequest("2026-11", FROM_MONTH, 40_000L);

        assertThat(failure(() -> budget.setBudget(stranger.token(), household, groceries, request)).code())
                .isEqualTo(404);
        assertThat(plans("2026-11")).isEmpty();
    }

    private void set(UUID category, String month, String scope, Long cents) {
        budget.setBudget(alice.token(), household, category, new CategoryBudgetRequest(month, scope, cents));
    }

    private int code(UUID category, String month, String scope, Long cents) {
        return failure(() -> set(category, month, scope, cents)).code();
    }

    private List<Plan> plans(String month) {
        return budget.month(alice.token(), household, month).plans();
    }
}
