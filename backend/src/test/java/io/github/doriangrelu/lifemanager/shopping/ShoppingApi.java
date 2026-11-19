package io.github.doriangrelu.lifemanager.shopping;

import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.ShoppingItemResponse;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.ShoppingListResponse;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Appels de l'API des courses pour les tests d'intégration. */
final class ShoppingApi {

    private final BlockingHttpClient http;

    ShoppingApi(BlockingHttpClient http) {
        this.http = http;
    }

    List<ShoppingListResponse> lists(String token, UUID household) {
        return http.retrieve(HttpRequest.GET(lists(household)).bearerAuth(token),
                Argument.listOf(ShoppingListResponse.class));
    }

    UUID listNamed(String token, UUID household, String name) {
        return lists(token, household).stream().filter(list -> list.name().equalsIgnoreCase(name)).findFirst()
                .orElseThrow().id();
    }

    ShoppingListResponse addList(String token, UUID household, String name) {
        return http.retrieve(HttpRequest.POST(lists(household), Map.of("name", name)).bearerAuth(token),
                ShoppingListResponse.class);
    }

    ShoppingItemResponse add(String token, UUID household, UUID list, String label) {
        return http.retrieve(HttpRequest.POST(items(household, list), Map.of("label", label)).bearerAuth(token),
                ShoppingItemResponse.class);
    }

    List<ShoppingItemResponse> items(String token, UUID household, UUID list) {
        return http.retrieve(HttpRequest.GET(items(household, list)).bearerAuth(token),
                Argument.listOf(ShoppingItemResponse.class));
    }

    void reorderItems(String token, UUID household, UUID list, List<UUID> ids) {
        http.exchange(HttpRequest.PUT(items(household, list) + "/order", Map.of("ids", ids)).bearerAuth(token));
    }

    void reorderLists(String token, UUID household, List<UUID> ids) {
        http.exchange(HttpRequest.PUT(lists(household) + "/order", Map.of("ids", ids)).bearerAuth(token));
    }

    private static String lists(UUID household) {
        return "/api/households/" + household + "/shopping-lists";
    }

    private static String items(UUID household, UUID list) {
        return lists(household) + "/" + list + "/items";
    }
}
