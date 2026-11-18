package io.github.doriangrelu.lifemanager.shopping;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
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
    void a_new_household_gets_three_empty_lists_sorted_by_name() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();

        assertThat(lists(owner, household)).extracting(ShoppingListResponse::name, ShoppingListResponse::pendingCount)
                .containsExactly(tuple("Bricolage", 0L), tuple("Courses", 0L), tuple("Vêtements", 0L));
    }

    @Test
    void lists_can_be_added_renamed_and_count_their_pending_items() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var pharmacy = addList(owner, household, "Pharma");
        add(owner, household, pharmacy.id(), "Doliprane");
        gesture(owner, household, pharmacy.id(), add(owner, household, pharmacy.id(), "Pansements").id(), "check");

        var renamed = rename(owner, household, pharmacy.id(), "Pharmacie");

        assertThat(renamed.pendingCount()).isEqualTo(1);
        assertThat(lists(owner, household)).extracting(ShoppingListResponse::name, ShoppingListResponse::pendingCount)
                .contains(tuple("Pharmacie", 1L));
        assertThat(rename(owner, household, pharmacy.id(), "PHARMACIE").name()).isEqualTo("PHARMACIE");
    }

    @Test
    void list_names_are_unique_whatever_the_case() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var tools = listNamed(owner, household, "Bricolage");

        assertThat(failure(() -> addList(owner, household, "courses")).code()).isEqualTo(409);
        assertThat(failure(() -> rename(owner, household, tools, "COURSES")).code()).isEqualTo(409);
        assertThat(failure(() -> addList(owner, household, "a".repeat(31))).code()).isEqualTo(400);
    }

    @Test
    void deleting_a_list_deletes_its_items() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var tools = listNamed(owner, household, "Bricolage");
        add(owner, household, tools, "Vis de 6");

        http.exchange(HttpRequest.DELETE(lists(household) + "/" + tools).bearerAuth(owner));

        assertThat(lists(owner, household)).extracting(ShoppingListResponse::name)
                .containsExactly("Courses", "Vêtements");
        assertThat(failure(() -> items(owner, household, tools)).code()).isEqualTo(404);
    }

    @Test
    void items_are_shared_and_listed_unchecked_first() {
        var owner = newUser();
        var guest = newUser();
        var household = households.create(owner, "Maison").id();
        households.join(guest, households.issueInvitation(owner, household).code());
        var groceries = listNamed(owner, household, "Courses");

        add(owner, household, groceries, "Pain");
        add(guest, household, groceries, "Lait");
        var eggs = add(owner, household, groceries, "Œufs");
        var checked = gesture(guest, household, groceries, eggs.id(), "check");

        assertThat(checked.checked()).isTrue();
        assertThat(items(guest, household, groceries))
                .extracting(ShoppingItemResponse::label, ShoppingItemResponse::checked)
                .containsExactly(tuple("Pain", false), tuple("Lait", false), tuple("Œufs", true));
    }

    @Test
    void lists_keep_their_items_apart() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var groceries = listNamed(owner, household, "Courses");
        var tools = listNamed(owner, household, "Bricolage");
        var screws = add(owner, household, tools, "Vis de 6").id();

        assertThat(items(owner, household, groceries)).isEmpty();
        assertThat(failure(() -> gesture(owner, household, groceries, screws, "check")).code()).isEqualTo(404);
    }

    @Test
    void checking_and_unchecking_are_idempotent() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var groceries = listNamed(owner, household, "Courses");
        var milk = add(owner, household, groceries, "Lait").id();

        gesture(owner, household, groceries, milk, "check");
        assertThat(gesture(owner, household, groceries, milk, "check").checked()).isTrue();
        gesture(owner, household, groceries, milk, "uncheck");
        assertThat(gesture(owner, household, groceries, milk, "uncheck").checked()).isFalse();
    }

    @Test
    void items_can_be_removed_one_by_one_or_all_checked_at_once() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var groceries = listNamed(owner, household, "Courses");
        var bread = add(owner, household, groceries, "Pain").id();
        gesture(owner, household, groceries, add(owner, household, groceries, "Lait").id(), "check");
        gesture(owner, household, groceries, add(owner, household, groceries, "Œufs").id(), "check");
        add(owner, household, groceries, "Beurre");

        http.exchange(HttpRequest.DELETE(items(household, groceries) + "/" + bread).bearerAuth(owner));
        clearChecked(owner, household, groceries);

        assertThat(items(owner, household, groceries)).extracting(ShoppingItemResponse::label)
                .containsExactly("Beurre");
    }

    @Test
    void invalid_labels_strangers_and_foreign_lists_are_refused() {
        var owner = newUser();
        var neighbour = newUser();
        var household = households.create(owner, "Maison").id();
        var groceries = listNamed(owner, household, "Courses");
        var neighbourHousehold = households.create(neighbour, "Chez le voisin").id();
        var neighbourList = listNamed(neighbour, neighbourHousehold, "Courses");

        assertThat(failure(() -> add(owner, household, groceries, " ")).code()).isEqualTo(400);
        assertThat(failure(() -> add(owner, household, groceries, "a".repeat(61))).code()).isEqualTo(400);
        assertThat(failure(() -> lists(neighbour, household)).code()).isEqualTo(404);
        assertThat(failure(() -> items(neighbour, household, groceries)).code()).isEqualTo(404);
        assertThat(failure(() -> items(owner, household, neighbourList)).code()).isEqualTo(404);
        assertThat(failure(() -> rename(owner, household, neighbourList, "À moi")).code()).isEqualTo(404);
        assertThat(failure(() -> gesture(owner, household, groceries, UUID.randomUUID(), "uncheck")).code())
                .isEqualTo(404);
    }

    private List<ShoppingListResponse> lists(String token, UUID household) {
        return http.retrieve(HttpRequest.GET(lists(household)).bearerAuth(token),
                Argument.listOf(ShoppingListResponse.class));
    }

    private UUID listNamed(String token, UUID household, String name) {
        return lists(token, household).stream().filter(list -> list.name().equals(name)).findFirst()
                .orElseThrow().id();
    }

    private ShoppingListResponse addList(String token, UUID household, String name) {
        return http.retrieve(HttpRequest.POST(lists(household), Map.of("name", name)).bearerAuth(token),
                ShoppingListResponse.class);
    }

    private ShoppingListResponse rename(String token, UUID household, UUID list, String name) {
        return http.retrieve(HttpRequest.PUT(lists(household) + "/" + list, Map.of("name", name)).bearerAuth(token),
                ShoppingListResponse.class);
    }

    private void clearChecked(String token, UUID household, UUID list) {
        http.exchange(HttpRequest.POST(lists(household) + "/" + list + "/clear-checked", "").bearerAuth(token));
    }

    private ShoppingItemResponse add(String token, UUID household, UUID list, String label) {
        return http.retrieve(HttpRequest.POST(items(household, list), Map.of("label", label)).bearerAuth(token),
                ShoppingItemResponse.class);
    }

    private ShoppingItemResponse gesture(String token, UUID household, UUID list, UUID item, String action) {
        var uri = items(household, list) + "/" + item + "/" + action;
        return http.retrieve(HttpRequest.POST(uri, "").bearerAuth(token), ShoppingItemResponse.class);
    }

    private List<ShoppingItemResponse> items(String token, UUID household, UUID list) {
        return http.retrieve(HttpRequest.GET(items(household, list)).bearerAuth(token),
                Argument.listOf(ShoppingItemResponse.class));
    }

    private static String lists(UUID household) {
        return "/api/households/" + household + "/shopping-lists";
    }

    private static String items(UUID household, UUID list) {
        return lists(household) + "/" + list + "/items";
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
