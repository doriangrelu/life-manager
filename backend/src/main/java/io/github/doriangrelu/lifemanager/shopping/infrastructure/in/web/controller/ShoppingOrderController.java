package io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.ReorderShoppingItems;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.ReorderShoppingLists;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ReorderShoppingItemsCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ReorderShoppingListsCommand;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.OrderRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

import java.util.UUID;

/** Ordre choisi par le foyer : sous-listes entre elles, articles d'une sous-liste (glisser-déposer de la PWA). */
@Controller("/api/households/{householdId}/shopping-lists")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class ShoppingOrderController {

    private final ReorderShoppingLists reorderLists;
    private final ReorderShoppingItems reorderItems;

    ShoppingOrderController(ReorderShoppingLists reorderLists, ReorderShoppingItems reorderItems) {
        this.reorderLists = reorderLists;
        this.reorderItems = reorderItems;
    }

    @Put("/order")
    HttpResponse<Void> reorderLists(Authentication user, UUID householdId, @Body @Valid OrderRequest request) {
        reorderLists.reorder(new ReorderShoppingListsCommand(user.getName(), householdId, request.ids()));
        return HttpResponse.noContent();
    }

    @Put("/{listId}/items/order")
    HttpResponse<Void> reorderItems(Authentication user, UUID householdId, UUID listId,
                                    @Body @Valid OrderRequest request) {
        reorderItems.reorder(new ReorderShoppingItemsCommand(user.getName(), householdId, listId, request.ids()));
        return HttpResponse.noContent();
    }
}
