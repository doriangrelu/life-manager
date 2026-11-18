package io.github.doriangrelu.lifemanager.budget;

import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.CategoryResponse;
import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BudgetCategoriesIntegrationTest extends IntegrationTestSupport {

    @Inject
    @Client("/")
    HttpClient client;

    BlockingHttpClient http;
    HouseholdApi households;

    @BeforeAll
    void setUp() {
        http = client.toBlocking();
        households = new HouseholdApi(http);
    }

    @Test
    void a_new_household_gets_the_default_categories() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();

        assertThat(list(owner, household)).extracting(CategoryResponse::name).containsExactly(
                "Abonnements", "Courses", "Logement", "Loisirs", "Restaurants", "Santé", "Transport", "Autre");
    }

    @Test
    void a_member_who_joins_gets_access_and_a_stranger_does_not() {
        var owner = newUser();
        var guest = newUser();
        var stranger = newUser();
        var household = households.create(owner, "Coloc").id();
        households.join(guest, households.issueInvitation(owner, household).code());

        assertThat(list(guest, household)).hasSize(8);
        assertThat(failure(() -> list(stranger, household)).code()).isEqualTo(404);
    }

    @Test
    void categories_can_be_added_but_names_stay_unique() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();

        var pets = add(owner, household, "Animaux");

        assertThat(pets.archived()).isFalse();
        assertThat(failure(() -> add(owner, household, "courses")).code()).isEqualTo(409);
        assertThat(failure(() -> add(owner, household, " ")).code()).isEqualTo(400);
        assertThat(failure(() -> add(owner, household, "a".repeat(41))).code()).isEqualTo(400);
    }

    @Test
    void a_category_can_be_renamed_archived_and_reactivated() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var leisure = categoryNamed(owner, household, "Loisirs");
        var base = "/api/households/" + household + "/expense-categories/" + leisure.id();

        var renamed = call(owner, HttpRequest.PUT(base, Map.of("name", "Sorties")));
        var archived = call(owner, HttpRequest.POST(base + "/archive", ""));
        var reactivated = call(owner, HttpRequest.POST(base + "/reactivate", ""));

        assertThat(renamed.name()).isEqualTo("Sorties");
        assertThat(archived.archived()).isTrue();
        assertThat(reactivated.archived()).isFalse();
        assertThat(failure(() -> call(owner, HttpRequest.PUT(base, Map.of("name", "Courses")))).code())
                .isEqualTo(409);
    }

    @Test
    void an_unknown_category_or_a_category_of_another_household_is_not_found() {
        var owner = newUser();
        var neighbour = newUser();
        var household = households.create(owner, "Maison").id();
        var neighbourHousehold = households.create(neighbour, "Chez le voisin").id();
        var neighbourCategory = categoryNamed(neighbour, neighbourHousehold, "Courses");

        var unknown = "/api/households/" + household + "/expense-categories/" + UUID.randomUUID() + "/archive";
        var foreign = "/api/households/" + household + "/expense-categories/" + neighbourCategory.id() + "/archive";
        assertThat(failure(() -> call(owner, HttpRequest.POST(unknown, ""))).code()).isEqualTo(404);
        assertThat(failure(() -> call(owner, HttpRequest.POST(foreign, ""))).code()).isEqualTo(404);
    }

    private List<CategoryResponse> list(String token, UUID household) {
        return http.retrieve(HttpRequest.GET("/api/households/" + household + "/expense-categories").bearerAuth(token),
                Argument.listOf(CategoryResponse.class));
    }

    private CategoryResponse add(String token, UUID household, String name) {
        return call(token, HttpRequest.POST("/api/households/" + household + "/expense-categories",
                Map.of("name", name)));
    }

    private CategoryResponse categoryNamed(String token, UUID household, String name) {
        return list(token, household).stream().filter(c -> c.name().equals(name)).findFirst().orElseThrow();
    }

    private CategoryResponse call(String token, io.micronaut.http.MutableHttpRequest<?> request) {
        return http.retrieve(request.bearerAuth(token), CategoryResponse.class);
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
