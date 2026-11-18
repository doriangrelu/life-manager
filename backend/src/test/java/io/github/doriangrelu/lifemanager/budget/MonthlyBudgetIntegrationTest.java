package io.github.doriangrelu.lifemanager.budget;

import io.github.doriangrelu.lifemanager.budget.BudgetTestUsers.TestUser;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.ExpenseRequest;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.ExpenseResponse;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.MonthlyBudgetResponse.CategoryTotal;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.MonthlyBudgetResponse.MemberTotal;
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
class MonthlyBudgetIntegrationTest extends IntegrationTestSupport {

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
    void the_month_is_totalled_by_category_from_largest_to_smallest() {
        var groceries = record("Courses", 3000, LocalDate.of(2026, 10, 3), alice.id(), null);
        record("Courses", 2500, LocalDate.of(2026, 10, 20), alice.id(), null);
        var transport = record("Transport", 4500, LocalDate.of(2026, 10, 31), alice.id(), null);
        record("Courses", 9999, LocalDate.of(2026, 11, 1), alice.id(), null);

        var month = budget.month(alice.token(), household, "2026-10");

        assertThat(month.totalCents()).isEqualTo(10_000);
        assertThat(month.byCategory()).containsExactly(
                new CategoryTotal(groceries.categoryId(), "Courses", 5500),
                new CategoryTotal(transport.categoryId(), "Transport", 4500));
        assertThat(month.expenses()).extracting(ExpenseResponse::spentOn).containsExactly(
                LocalDate.of(2026, 10, 31), LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 3));
    }

    @Test
    void the_month_is_totalled_by_payer_and_by_beneficiary() {
        record("Courses", 6000, LocalDate.of(2026, 10, 5), alice.id(), null);
        record("Loisirs", 4000, LocalDate.of(2026, 10, 6), bob.id(), alice.id());

        var month = budget.month(bob.token(), household, "2026-10");

        assertThat(month.byPayer()).containsExactly(
                new MemberTotal(alice.id(), 6000), new MemberTotal(bob.id(), 4000));
        assertThat(month.byBeneficiary()).containsExactly(
                new MemberTotal(null, 6000), new MemberTotal(alice.id(), 4000));
    }

    @Test
    void a_month_without_expense_is_empty() {
        var month = budget.month(alice.token(), household, "2026-09");

        assertThat(month.month()).isEqualTo("2026-09");
        assertThat(month.totalCents()).isZero();
        assertThat(month.byCategory()).isEmpty();
        assertThat(month.byPayer()).isEmpty();
        assertThat(month.expenses()).isEmpty();
    }

    @Test
    void an_invalid_month_is_refused() {
        assertThat(failure(() -> budget.month(alice.token(), household, "2026-13")).code()).isEqualTo(400);
    }

    private ExpenseResponse record(String category, long cents, LocalDate day, UUID paidBy, UUID beneficiary) {
        var categoryId = budget.category(alice.token(), household, category);
        return budget.record(alice.token(), household,
                new ExpenseRequest(cents, categoryId, null, day, paidBy, beneficiary, null));
    }
}
