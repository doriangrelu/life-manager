package io.github.doriangrelu.lifemanager.budget;

import io.github.doriangrelu.lifemanager.budget.BudgetTestUsers.TestUser;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.CategoryResponse;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static io.github.doriangrelu.lifemanager.budget.BudgetTestUsers.newUser;
import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

/** Catégorie tapée librement à la saisie d'une dépense, et place de « Autre » dans la liste. */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FreeCategoryIntegrationTest extends IntegrationTestSupport {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 12);

    @Inject
    @Client("/")
    HttpClient client;

    BudgetApi budget;
    TestUser alice;
    UUID household;

    @BeforeEach
    void newHousehold() {
        budget = new BudgetApi(client.toBlocking());
        alice = newUser(KEYCLOAK, "Alice");
        household = new HouseholdApi(client.toBlocking()).create(alice.token(), "Maison").id();
    }

    @Test
    void other_is_always_listed_last() {
        assertThat(names()).containsExactly(
                "Abonnements", "Courses", "Logement", "Loisirs", "Restaurants", "Santé", "Transport", "Autre");
    }

    @Test
    void a_new_name_creates_the_category_with_the_expense() {
        var expense = budget.record(alice.token(), household, withName(2500, "Cadeaux"));

        var gifts = budget.category(alice.token(), household, "Cadeaux");
        assertThat(expense.categoryId()).isEqualTo(gifts);
        assertThat(names()).containsSubsequence("Cadeaux", "Autre");
    }

    @Test
    void an_existing_name_is_reused_whatever_the_case() {
        var expense = budget.record(alice.token(), household, withName(1000, "courses"));

        assertThat(expense.categoryId()).isEqualTo(budget.category(alice.token(), household, "Courses"));
        assertThat(names()).hasSize(8);
    }

    @Test
    void an_archived_category_is_reactivated() {
        var subscriptions = budget.category(alice.token(), household, "Abonnements");
        budget.archive(alice.token(), household, subscriptions);

        var expense = budget.record(alice.token(), household, withName(999, "Abonnements"));

        assertThat(expense.categoryId()).isEqualTo(subscriptions);
        assertThat(budget.categories(alice.token(), household))
                .filteredOn(category -> category.id().equals(subscriptions))
                .singleElement().satisfies(category -> assertThat(category.archived()).isFalse());
    }

    @Test
    void a_refused_expense_creates_no_category() {
        assertThat(failure(() -> budget.record(alice.token(), household, withName(0, "Cadeaux"))).code())
                .isEqualTo(400);
        assertThat(names()).doesNotContain("Cadeaux");
    }

    @Test
    void the_category_is_given_one_way_only() {
        var groceries = budget.category(alice.token(), household, "Courses");
        var both = new ExpenseRequest(100, groceries, "Cadeaux", DAY, alice.id(), null, null);
        var neither = new ExpenseRequest(100, null, null, DAY, alice.id(), null, null);

        assertThat(failure(() -> budget.record(alice.token(), household, both)).code()).isEqualTo(400);
        assertThat(failure(() -> budget.record(alice.token(), household, neither)).code()).isEqualTo(400);
    }

    @Test
    void two_members_typing_the_same_new_name_at_once_get_one_category() {
        var first = CompletableFuture.supplyAsync(() -> budget.record(alice.token(), household, withName(100, "Jardin")));
        var second = CompletableFuture.supplyAsync(() -> budget.record(alice.token(), household, withName(200, "jardin")));

        assertThat(first.join().categoryId()).isEqualTo(second.join().categoryId());
        assertThat(names()).filteredOn(name -> name.equalsIgnoreCase("jardin")).hasSize(1);
    }

    private ExpenseRequest withName(long cents, String categoryName) {
        return new ExpenseRequest(cents, null, categoryName, DAY, alice.id(), null, null);
    }

    private List<String> names() {
        return budget.categories(alice.token(), household).stream().map(CategoryResponse::name).toList();
    }
}
