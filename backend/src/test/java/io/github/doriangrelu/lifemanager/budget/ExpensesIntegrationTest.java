package io.github.doriangrelu.lifemanager.budget;

import io.github.doriangrelu.lifemanager.budget.BudgetTestUsers.TestUser;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.ExpenseRequest;
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
import java.time.ZoneId;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.budget.BudgetTestUsers.newUser;
import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpensesIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 12);

    @Inject
    @Client("/")
    HttpClient client;

    BudgetApi budget;
    TestUser alice;
    TestUser bob;
    UUID household;
    UUID groceries;

    // Chaque test part d'un foyer neuf : Alice l'a créé, Bob l'a rejoint.
    @BeforeEach
    void newHousehold() {
        var households = new HouseholdApi(client.toBlocking());
        budget = new BudgetApi(client.toBlocking());
        alice = newUser(KEYCLOAK, "Alice");
        bob = newUser(KEYCLOAK, "Bob");
        household = households.create(alice.token(), "Maison").id();
        households.join(bob.token(), households.issueInvitation(alice.token(), household).code());
        groceries = budget.category(alice.token(), household, "Courses");
    }

    @Test
    void an_expense_is_recorded_corrected_and_deleted() {
        var recorded = budget.record(alice.token(), household, expense(4250, alice.id(), null));
        assertThat(recorded.amountCents()).isEqualTo(4250);
        assertThat(recorded.beneficiary()).isNull();

        var corrected = budget.correct(bob.token(), household, recorded.id(), expense(2450, alice.id(), bob.id()));
        assertThat(corrected.amountCents()).isEqualTo(2450);
        assertThat(corrected.beneficiary()).isEqualTo(bob.id());

        budget.delete(alice.token(), household, recorded.id());
        assertThat(failure(() -> budget.delete(alice.token(), household, recorded.id())).code()).isEqualTo(404);
    }

    @Test
    void invalid_amounts_and_future_dates_are_refused() {
        var tomorrow = LocalDate.now(ZoneId.of("Europe/Paris")).plusDays(1);
        var future = new ExpenseRequest(100, groceries, tomorrow, alice.id(), null, null);

        assertThat(failure(() -> budget.record(alice.token(), household, expense(0, alice.id(), null))).code())
                .isEqualTo(400);
        assertThat(failure(() -> budget.record(alice.token(), household, expense(100_000_001, alice.id(), null)))
                .code()).isEqualTo(400);
        assertThat(failure(() -> budget.record(alice.token(), household, future)).code()).isEqualTo(400);
    }

    @Test
    void payer_and_beneficiary_must_be_members() {
        var stranger = newUser(KEYCLOAK, "Inconnu");

        assertThat(failure(() -> budget.record(alice.token(), household, expense(100, stranger.id(), null))).code())
                .isEqualTo(400);
        assertThat(failure(() -> budget.record(alice.token(), household, expense(100, alice.id(), stranger.id())))
                .code()).isEqualTo(400);
    }

    @Test
    void only_an_active_category_of_the_household_can_be_used() {
        var neighbour = newUser(KEYCLOAK, "Voisin");
        var neighbourHousehold = new HouseholdApi(client.toBlocking()).create(neighbour.token(), "Voisins").id();
        var foreignCategory = budget.category(neighbour.token(), neighbourHousehold, "Courses");
        budget.archive(alice.token(), household, groceries);

        var archived = expense(100, alice.id(), null);
        var foreign = new ExpenseRequest(100, foreignCategory, DAY, alice.id(), null, null);
        assertThat(failure(() -> budget.record(alice.token(), household, archived)).code()).isEqualTo(400);
        assertThat(failure(() -> budget.record(alice.token(), household, foreign)).code()).isEqualTo(400);
    }

    @Test
    void a_refused_correction_changes_nothing() {
        var recorded = budget.record(alice.token(), household, expense(4250, alice.id(), null));

        var error = failure(() -> budget.correct(alice.token(), household, recorded.id(),
                expense(-5, alice.id(), null)));

        assertThat(error.code()).isEqualTo(400);
        assertThat(budget.month(alice.token(), household, "2026-10").expenses())
                .singleElement().satisfies(expense -> assertThat(expense.amountCents()).isEqualTo(4250));
    }

    @Test
    void expenses_are_invisible_outside_the_household() {
        var stranger = newUser(KEYCLOAK, "Inconnu");
        var recorded = budget.record(alice.token(), household, expense(100, alice.id(), null));

        assertThat(failure(() -> budget.record(stranger.token(), household, expense(100, stranger.id(), null)))
                .code()).isEqualTo(404);
        assertThat(failure(() -> budget.delete(stranger.token(), household, recorded.id())).code()).isEqualTo(404);
        assertThat(failure(() -> budget.month(stranger.token(), household, "2026-10")).code()).isEqualTo(404);
    }

    private ExpenseRequest expense(long cents, UUID paidBy, UUID beneficiary) {
        return new ExpenseRequest(cents, groceries, DAY, paidBy, beneficiary, "Marché");
    }
}
