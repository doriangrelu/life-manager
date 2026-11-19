package io.github.doriangrelu.lifemanager.budget;

import io.github.doriangrelu.lifemanager.budget.BudgetTestUsers.TestUser;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.CategoryResponse;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.ExpenseRequest;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.IncomeRequest;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.IncomeResponse;
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
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.budget.BudgetTestUsers.newUser;
import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IncomesIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 28);

    @Inject
    @Client("/")
    HttpClient client;

    BudgetApi budget;
    TestUser alice;
    TestUser bob;
    UUID household;

    @BeforeEach
    void newHousehold() {
        var households = new HouseholdApi(client.toBlocking());
        budget = new BudgetApi(client.toBlocking());
        alice = newUser(KEYCLOAK, "Alice");
        bob = newUser(KEYCLOAK, "Bob");
        household = households.create(alice.token(), "Maison").id();
        households.join(bob.token(), households.issueInvitation(alice.token(), household).code());
    }

    @Test
    void income_categories_are_suggested_once_and_kept_apart_from_expense_ones() {
        var first = budget.incomeCategories(alice.token(), household);
        var again = budget.incomeCategories(bob.token(), household);

        assertThat(first).extracting(CategoryResponse::name)
                .containsExactly("aides", "prime", "remboursement", "salaire", "autre");
        assertThat(again).isEqualTo(first);
        assertThat(budget.categories(alice.token(), household)).hasSize(8); // « autre » existe des deux côtés
    }

    @Test
    void an_income_is_recorded_corrected_and_deleted() {
        var salary = budget.incomeCategory(alice.token(), household, "Salaire");

        var recorded = budget.recordIncome(alice.token(), household,
                new IncomeRequest(245_000, salary, null, DAY, bob.id(), "novembre"));
        var corrected = budget.correctIncome(alice.token(), household, recorded.id(),
                new IncomeRequest(250_000, null, "Prime de fin d'année", DAY, null, null));

        assertThat(recorded.receivedBy()).isEqualTo(bob.id());
        assertThat(corrected.amountCents()).isEqualTo(250_000);
        assertThat(corrected.receivedBy()).isNull();
        budget.deleteIncome(alice.token(), household, recorded.id());
        assertThat(failure(() -> budget.deleteIncome(alice.token(), household, recorded.id())).code())
                .isEqualTo(404);
    }

    @Test
    void an_expense_category_cannot_classify_an_income_nor_the_reverse() {
        var groceries = budget.category(alice.token(), household, "Courses");
        var salary = budget.incomeCategory(alice.token(), household, "Salaire");

        assertThat(failure(() -> income(groceries, 100)).code()).isEqualTo(400);
        assertThat(failure(() -> budget.record(alice.token(), household,
                new ExpenseRequest(100, salary, null, DAY, alice.id(), null, null))).code()).isEqualTo(400);
    }

    @Test
    void the_month_shows_incomes_and_its_result() {
        var salary = budget.incomeCategory(alice.token(), household, "Salaire");
        income(salary, 300_000);
        expense(240_000, DAY);

        var month = budget.month(alice.token(), household, "2026-10");

        assertThat(month.incomeCents()).isEqualTo(300_000);
        assertThat(month.totalCents()).isEqualTo(240_000);
        assertThat(month.resultCents()).isEqualTo(60_000);
        assertThat(month.incomes()).extracting(IncomeResponse::amountCents).containsExactly(300_000L);
    }

    @Test
    void the_report_sums_a_period_month_by_month() {
        var salary = budget.incomeCategory(alice.token(), household, "Salaire");
        income(salary, 300_000);
        expense(100_000, LocalDate.of(2026, 9, 2));
        expense(240_000, DAY);

        var quarter = budget.report(alice.token(), household, "2026-08", "2026-10");

        assertThat(quarter.incomeCents()).isEqualTo(300_000);
        assertThat(quarter.expenseCents()).isEqualTo(340_000);
        assertThat(quarter.resultCents()).isEqualTo(-40_000);
        assertThat(quarter.months()).extracting(m -> m.month() + " " + m.resultCents())
                .containsExactly("2026-09 -100000", "2026-10 60000");
        assertThat(quarter.incomeByReceiver()).extracting(t -> t.totalCents()).containsExactly(300_000L);
    }

    @Test
    void an_invalid_period_or_a_stranger_is_refused() {
        var stranger = newUser(KEYCLOAK, "Eve");

        assertThat(failure(() -> budget.report(alice.token(), household, "2026-10", "2026-09")).code())
                .isEqualTo(400);
        assertThat(failure(() -> budget.report(alice.token(), household, "2024-01", "2026-01")).code())
                .isEqualTo(400);
        assertThat(failure(() -> budget.report(stranger.token(), household, "2026-10", "2026-10")).code())
                .isEqualTo(404);
        assertThat(failure(() -> budget.incomeCategories(stranger.token(), household)).code()).isEqualTo(404);
        var salary = budget.incomeCategory(alice.token(), household, "Salaire");
        assertThat(failure(() -> budget.recordIncome(alice.token(), household,
                new IncomeRequest(100, salary, null, DAY, stranger.id(), null))).code()).isEqualTo(400);
    }

    private IncomeResponse income(UUID category, long cents) {
        return budget.recordIncome(alice.token(), household, new IncomeRequest(cents, category, null, DAY, null, null));
    }

    private void expense(long cents, LocalDate day) {
        var groceries = budget.category(alice.token(), household, "Courses");
        budget.record(alice.token(), household, new ExpenseRequest(cents, groceries, null, day, alice.id(), null, null));
    }
}
