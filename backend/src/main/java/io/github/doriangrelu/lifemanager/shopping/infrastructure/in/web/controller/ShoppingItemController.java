package io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.AddShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.CheckShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.ClearCheckedItems;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.ListShoppingItems;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.RemoveShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.UncheckShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.AddShoppingItemCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ClearCheckedItemsCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ShoppingItemCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.query.ListShoppingItemsQuery;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.ShoppingItemRequest;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.ShoppingItemResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

/** Liste de courses d'un foyer : consultation, ajout, cochage, suppression, vidage des articles cochés. */
@Controller("/api/households/{householdId}/shopping-items")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class ShoppingItemController {

    private final ListShoppingItems listItems;
    private final AddShoppingItem addItem;
    private final CheckShoppingItem checkItem;
    private final UncheckShoppingItem uncheckItem;
    private final RemoveShoppingItem removeItem;
    private final ClearCheckedItems clearChecked;

    ShoppingItemController(ListShoppingItems listItems, AddShoppingItem addItem, CheckShoppingItem checkItem,
                           UncheckShoppingItem uncheckItem, RemoveShoppingItem removeItem,
                           ClearCheckedItems clearChecked) {
        this.listItems = listItems;
        this.addItem = addItem;
        this.checkItem = checkItem;
        this.uncheckItem = uncheckItem;
        this.removeItem = removeItem;
        this.clearChecked = clearChecked;
    }

    @Get
    List<ShoppingItemResponse> list(Authentication user, UUID householdId) {
        return listItems.list(new ListShoppingItemsQuery(user.getName(), householdId)).stream()
                .map(ShoppingItemResponse::from)
                .toList();
    }

    @Post
    HttpResponse<ShoppingItemResponse> add(Authentication user, UUID householdId,
                                           @Body @Valid ShoppingItemRequest request) {
        var added = addItem.add(new AddShoppingItemCommand(user.getName(), householdId, request.label()));
        return HttpResponse.created(ShoppingItemResponse.from(added));
    }

    @Post("/{itemId}/check")
    ShoppingItemResponse check(Authentication user, UUID householdId, UUID itemId) {
        return ShoppingItemResponse.from(checkItem.check(new ShoppingItemCommand(user.getName(), householdId, itemId)));
    }

    @Post("/{itemId}/uncheck")
    ShoppingItemResponse uncheck(Authentication user, UUID householdId, UUID itemId) {
        var command = new ShoppingItemCommand(user.getName(), householdId, itemId);
        return ShoppingItemResponse.from(uncheckItem.uncheck(command));
    }

    @Delete("/{itemId}")
    HttpResponse<Void> remove(Authentication user, UUID householdId, UUID itemId) {
        removeItem.remove(new ShoppingItemCommand(user.getName(), householdId, itemId));
        return HttpResponse.noContent();
    }

    @Post("/clear-checked")
    HttpResponse<Void> clearChecked(Authentication user, UUID householdId) {
        clearChecked.clear(new ClearCheckedItemsCommand(user.getName(), householdId));
        return HttpResponse.noContent();
    }
}
