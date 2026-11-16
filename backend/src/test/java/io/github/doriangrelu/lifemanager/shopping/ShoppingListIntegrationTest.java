package io.github.doriangrelu.lifemanager.shopping;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.ShoppingItemResponse;
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
import static org.assertj.core.api.Assertions.tuple;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShoppingListIntegrationTest extends IntegrationTestSupport {

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
    void items_are_shared_and_listed_unchecked_first() {
        var owner = newUser();
        var guest = newUser();
        var household = households.create(owner, "Maison").id();
        households.join(guest, households.issueInvitation(owner, household).code());

        add(owner, household, "Pain");
        add(guest, household, "Lait");
        var eggs = add(owner, household, "Œufs");
        var checked = gesture(guest, household, eggs.id(), "check");

        assertThat(checked.checked()).isTrue();
        assertThat(list(guest, household)).extracting(ShoppingItemResponse::label, ShoppingItemResponse::checked)
                .containsExactly(tuple("Pain", false), tuple("Lait", false), tuple("Œufs", true));
    }

    @Test
    void checking_and_unchecking_are_idempotent() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var milk = add(owner, household, "Lait").id();

        gesture(owner, household, milk, "check");
        assertThat(gesture(owner, household, milk, "check").checked()).isTrue();
        gesture(owner, household, milk, "uncheck");
        assertThat(gesture(owner, household, milk, "uncheck").checked()).isFalse();
    }

    @Test
    void items_can_be_removed_one_by_one_or_all_checked_at_once() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var bread = add(owner, household, "Pain").id();
        gesture(owner, household, add(owner, household, "Lait").id(), "check");
        gesture(owner, household, add(owner, household, "Œufs").id(), "check");
        add(owner, household, "Beurre");

        http.exchange(HttpRequest.DELETE(base(household) + "/" + bread).bearerAuth(owner));
        http.exchange(HttpRequest.POST(base(household) + "/clear-checked", "").bearerAuth(owner));

        assertThat(list(owner, household)).extracting(ShoppingItemResponse::label).containsExactly("Beurre");
    }

    @Test
    void invalid_labels_strangers_and_foreign_items_are_refused() {
        var owner = newUser();
        var neighbour = newUser();
        var household = households.create(owner, "Maison").id();
        var neighbourHousehold = households.create(neighbour, "Chez le voisin").id();
        var neighbourItem = add(neighbour, neighbourHousehold, "Café").id();

        assertThat(failure(() -> add(owner, household, " ")).code()).isEqualTo(400);
        assertThat(failure(() -> add(owner, household, "a".repeat(61))).code()).isEqualTo(400);
        assertThat(failure(() -> list(neighbour, household)).code()).isEqualTo(404);
        assertThat(failure(() -> gesture(owner, household, neighbourItem, "check")).code()).isEqualTo(404);
        assertThat(failure(() -> gesture(owner, household, UUID.randomUUID(), "uncheck")).code()).isEqualTo(404);
    }

    private ShoppingItemResponse add(String token, UUID household, String label) {
        return http.retrieve(HttpRequest.POST(base(household), Map.of("label", label)).bearerAuth(token),
                ShoppingItemResponse.class);
    }

    private ShoppingItemResponse gesture(String token, UUID household, UUID item, String action) {
        return http.retrieve(HttpRequest.POST(base(household) + "/" + item + "/" + action, "").bearerAuth(token),
                ShoppingItemResponse.class);
    }

    private List<ShoppingItemResponse> list(String token, UUID household) {
        return http.retrieve(HttpRequest.GET(base(household)).bearerAuth(token),
                Argument.listOf(ShoppingItemResponse.class));
    }

    private static String base(UUID household) {
        return "/api/households/" + household + "/shopping-items";
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
