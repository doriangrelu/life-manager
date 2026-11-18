package io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.AddShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.CheckShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.ListShoppingItems;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.RemoveShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.UncheckShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.AddShoppingItemCommand;
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

/** Articles d'une sous-liste de courses : consultation, ajout, cochage, suppression. */
@Controller("/api/households/{householdId}/shopping-lists/{listId}/items")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class ShoppingItemController {

    private final ListShoppingItems listItems;
    private final AddShoppingItem addItem;
    private final CheckShoppingItem checkItem;
    private final UncheckShoppingItem uncheckItem;
    private final RemoveShoppingItem removeItem;

    ShoppingItemController(ListShoppingItems listItems, AddShoppingItem addItem, CheckShoppingItem checkItem,
                           UncheckShoppingItem uncheckItem, RemoveShoppingItem removeItem) {
        this.listItems = listItems;
        this.addItem = addItem;
        this.checkItem = checkItem;
        this.uncheckItem = uncheckItem;
        this.removeItem = removeItem;
    }

    @Get
    List<ShoppingItemResponse> list(Authentication user, UUID householdId, UUID listId) {
        return listItems.list(new ListShoppingItemsQuery(user.getName(), householdId, listId)).stream()
                .map(ShoppingItemResponse::from)
                .toList();
    }

    @Post
    HttpResponse<ShoppingItemResponse> add(Authentication user, UUID householdId, UUID listId,
                                           @Body @Valid ShoppingItemRequest request) {
        var command = new AddShoppingItemCommand(user.getName(), householdId, listId, request.label());
        return HttpResponse.created(ShoppingItemResponse.from(addItem.add(command)));
    }

    @Post("/{itemId}/check")
    ShoppingItemResponse check(Authentication user, UUID householdId, UUID listId, UUID itemId) {
        var command = new ShoppingItemCommand(user.getName(), householdId, listId, itemId);
        return ShoppingItemResponse.from(checkItem.check(command));
    }

    @Post("/{itemId}/uncheck")
    ShoppingItemResponse uncheck(Authentication user, UUID householdId, UUID listId, UUID itemId) {
        var command = new ShoppingItemCommand(user.getName(), householdId, listId, itemId);
        return ShoppingItemResponse.from(uncheckItem.uncheck(command));
    }

    @Delete("/{itemId}")
    HttpResponse<Void> remove(Authentication user, UUID householdId, UUID listId, UUID itemId) {
        removeItem.remove(new ShoppingItemCommand(user.getName(), householdId, listId, itemId));
        return HttpResponse.noContent();
    }
}
