package io.github.doriangrelu.lifemanager.shopping;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.ShoppingItemResponse;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.ShoppingListResponse;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

/** Ordre des courses choisi par le foyer : articles d'une sous-liste, sous-listes entre elles. */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShoppingOrderIntegrationTest extends IntegrationTestSupport {

    @Inject
    @Client("/")
    HttpClient client;

    HouseholdApi households;
    ShoppingApi shopping;

    @BeforeAll
    void setUp() {
        households = new HouseholdApi(client.toBlocking());
        shopping = new ShoppingApi(client.toBlocking());
    }

    @Test
    void items_can_be_reordered_and_new_ones_come_last() {
        var owner = newUser();
        var guest = newUser();
        var household = households.create(owner, "Maison").id();
        households.join(guest, households.issueInvitation(owner, household).code());
        var groceries = shopping.listNamed(owner, household, "Courses");
        var bread = shopping.add(owner, household, groceries, "Pain").id();
        var milk = shopping.add(owner, household, groceries, "Lait").id();
        var butter = shopping.add(owner, household, groceries, "Beurre").id();

        // Seul « Beurre » est déplacé : les autres gardent leur ordre relatif derrière lui.
        shopping.reorderItems(owner, household, groceries, List.of(butter));
        shopping.add(owner, household, groceries, "Œufs");

        assertThat(shopping.items(guest, household, groceries)).extracting(ShoppingItemResponse::label)
                .containsExactly("beurre", "pain", "lait", "œufs");
        shopping.reorderItems(owner, household, groceries, List.of(milk, bread));
        assertThat(shopping.items(guest, household, groceries)).extracting(ShoppingItemResponse::label)
                .containsExactly("lait", "pain", "beurre", "œufs");
    }

    @Test
    void lists_can_be_reordered_and_a_new_one_comes_last() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var tools = shopping.listNamed(owner, household, "Bricolage");

        shopping.reorderLists(owner, household, List.of(tools));
        shopping.addList(owner, household, "Pharmacie");

        assertThat(shopping.lists(owner, household)).extracting(ShoppingListResponse::name)
                .containsExactly("bricolage", "courses", "vêtements", "pharmacie");
    }

    @Test
    void reordering_with_a_foreign_item_or_list_changes_nothing() {
        var owner = newUser();
        var stranger = newUser();
        var household = households.create(owner, "Maison").id();
        var groceries = shopping.listNamed(owner, household, "Courses");
        var tools = shopping.listNamed(owner, household, "Bricolage");
        shopping.add(owner, household, groceries, "Pain");
        var screws = shopping.add(owner, household, tools, "Vis de 6").id();

        assertThat(failure(() -> shopping.reorderItems(owner, household, groceries, List.of(screws))).code())
                .isEqualTo(404);
        assertThat(failure(() -> shopping.reorderLists(owner, household, List.of(UUID.randomUUID()))).code())
                .isEqualTo(404);
        assertThat(failure(() -> shopping.reorderLists(stranger, household, List.of(tools))).code())
                .isEqualTo(404);
        assertThat(shopping.lists(owner, household)).extracting(ShoppingListResponse::name)
                .containsExactly("courses", "bricolage", "vêtements");
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
