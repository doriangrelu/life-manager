package io.github.doriangrelu.lifemanager.shopping;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.PurchaseHistoryEntryResponse;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.ShoppingItemResponse;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.ShoppingListResponse;
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
class PurchaseHistoryIntegrationTest extends IntegrationTestSupport {

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
    void clearing_checked_items_fills_the_history() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var groceries = listNamed(owner, household, "Courses");
        buy(owner, household, groceries, "Lait", "Pain");
        add(owner, household, groceries, "Beurre");

        assertThat(history(owner, household, groceries))
                .extracting(PurchaseHistoryEntryResponse::label, PurchaseHistoryEntryResponse::purchaseCount)
                .containsExactlyInAnyOrder(tuple("Lait", 1), tuple("Pain", 1));
    }

    @Test
    void an_item_bought_again_is_counted_once_more_and_comes_first() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var groceries = listNamed(owner, household, "Courses");
        buy(owner, household, groceries, "Lait", "Pain");

        buy(owner, household, groceries, "lait");

        var history = history(owner, household, groceries);
        assertThat(history).extracting(PurchaseHistoryEntryResponse::label, PurchaseHistoryEntryResponse::purchaseCount)
                .containsExactly(tuple("Lait", 2), tuple("Pain", 1));
        assertThat(history.getFirst().lastPurchasedAt()).isAfter(history.get(1).lastPurchasedAt());
    }

    @Test
    void each_list_has_its_own_history() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        buy(owner, household, listNamed(owner, household, "Courses"), "Lait");

        assertThat(history(owner, household, listNamed(owner, household, "Bricolage"))).isEmpty();
    }

    @Test
    void history_items_go_back_to_the_list_unchecked() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var groceries = listNamed(owner, household, "Courses");
        buy(owner, household, groceries, "Lait", "Pain", "Œufs");
        add(owner, household, groceries, "lait");
        var ids = history(owner, household, groceries).stream().map(PurchaseHistoryEntryResponse::id).toList();

        var added = restock(owner, household, groceries, ids);

        // « lait » était déjà à acheter : il n'est pas ajouté une seconde fois.
        assertThat(added).extracting(ShoppingItemResponse::label).containsExactlyInAnyOrder("Pain", "Œufs");
        assertThat(items(owner, household, groceries)).extracting(ShoppingItemResponse::checked)
                .containsExactly(false, false, false);
    }

    @Test
    void restocking_is_all_or_nothing() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var groceries = listNamed(owner, household, "Courses");
        var tools = listNamed(owner, household, "Bricolage");
        buy(owner, household, groceries, "Lait");
        buy(owner, household, tools, "Vis");
        var milk = history(owner, household, groceries).getFirst().id();
        var screws = history(owner, household, tools).getFirst().id();

        assertThat(failure(() -> restock(owner, household, groceries, List.of(milk, UUID.randomUUID()))).code())
                .isEqualTo(404);
        assertThat(failure(() -> restock(owner, household, groceries, List.of(milk, screws))).code())
                .isEqualTo(404);
        assertThat(failure(() -> restock(owner, household, groceries, List.of())).code()).isEqualTo(400);
        assertThat(items(owner, household, groceries)).isEmpty();
    }

    @Test
    void an_entry_can_be_forgotten() {
        var owner = newUser();
        var neighbour = newUser();
        var household = households.create(owner, "Maison").id();
        var groceries = listNamed(owner, household, "Courses");
        buy(owner, household, groceries, "Couches", "Lait");
        var nappies = history(owner, household, groceries).stream()
                .filter(entry -> entry.label().equals("Couches")).findFirst().orElseThrow().id();

        assertThat(failure(() -> forget(neighbour, household, groceries, nappies)).code()).isEqualTo(404);
        forget(owner, household, groceries, nappies);

        assertThat(history(owner, household, groceries)).extracting(PurchaseHistoryEntryResponse::label)
                .containsExactly("Lait");
    }

    /** Ajoute, coche puis vide les articles : ils sont achetés. */
    private void buy(String token, UUID household, UUID list, String... labels) {
        for (String label : labels) {
            var item = add(token, household, list, label).id();
            http.exchange(HttpRequest.POST(items(household, list) + "/" + item + "/check", "").bearerAuth(token));
        }
        http.exchange(HttpRequest.POST(lists(household) + "/" + list + "/clear-checked", "").bearerAuth(token));
    }

    private ShoppingItemResponse add(String token, UUID household, UUID list, String label) {
        return http.retrieve(HttpRequest.POST(items(household, list), Map.of("label", label)).bearerAuth(token),
                ShoppingItemResponse.class);
    }

    private List<ShoppingItemResponse> items(String token, UUID household, UUID list) {
        return http.retrieve(HttpRequest.GET(items(household, list)).bearerAuth(token),
                Argument.listOf(ShoppingItemResponse.class));
    }

    private List<PurchaseHistoryEntryResponse> history(String token, UUID household, UUID list) {
        return http.retrieve(HttpRequest.GET(history(household, list)).bearerAuth(token),
                Argument.listOf(PurchaseHistoryEntryResponse.class));
    }

    private List<ShoppingItemResponse> restock(String token, UUID household, UUID list, List<UUID> entryIds) {
        var request = HttpRequest.POST(history(household, list) + "/restock", Map.of("entryIds", entryIds));
        return http.retrieve(request.bearerAuth(token), Argument.listOf(ShoppingItemResponse.class));
    }

    private void forget(String token, UUID household, UUID list, UUID entry) {
        http.exchange(HttpRequest.DELETE(history(household, list) + "/" + entry).bearerAuth(token));
    }

    private UUID listNamed(String token, UUID household, String name) {
        return http.retrieve(HttpRequest.GET(lists(household)).bearerAuth(token),
                        Argument.listOf(ShoppingListResponse.class)).stream()
                .filter(list -> list.name().equals(name)).findFirst().orElseThrow().id();
    }

    private static String lists(UUID household) {
        return "/api/households/" + household + "/shopping-lists";
    }

    private static String items(UUID household, UUID list) {
        return lists(household) + "/" + list + "/items";
    }

    private static String history(UUID household, UUID list) {
        return lists(household) + "/" + list + "/history";
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
