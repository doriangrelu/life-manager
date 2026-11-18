package io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.AddShoppingList;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.ClearCheckedItems;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.DeleteShoppingList;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.ListShoppingLists;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.RenameShoppingList;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.AddShoppingListCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ClearCheckedItemsCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.RenameShoppingListCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ShoppingListCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.query.ListShoppingListsQuery;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.ShoppingListRequest;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.ShoppingListResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

/** Sous-listes de courses d'un foyer : consultation, ajout, renommage, suppression, vidage des articles cochés. */
@Controller("/api/households/{householdId}/shopping-lists")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class ShoppingListController {

    private final ListShoppingLists listLists;
    private final AddShoppingList addList;
    private final RenameShoppingList renameList;
    private final DeleteShoppingList deleteList;
    private final ClearCheckedItems clearChecked;

    ShoppingListController(ListShoppingLists listLists, AddShoppingList addList, RenameShoppingList renameList,
                           DeleteShoppingList deleteList, ClearCheckedItems clearChecked) {
        this.listLists = listLists;
        this.addList = addList;
        this.renameList = renameList;
        this.deleteList = deleteList;
        this.clearChecked = clearChecked;
    }

    @Get
    List<ShoppingListResponse> list(Authentication user, UUID householdId) {
        return listLists.list(new ListShoppingListsQuery(user.getName(), householdId)).stream()
                .map(ShoppingListResponse::from)
                .toList();
    }

    @Post
    HttpResponse<ShoppingListResponse> add(Authentication user, UUID householdId,
                                           @Body @Valid ShoppingListRequest request) {
        var added = addList.add(new AddShoppingListCommand(user.getName(), householdId, request.name()));
        return HttpResponse.created(ShoppingListResponse.from(added));
    }

    @Put("/{listId}")
    ShoppingListResponse rename(Authentication user, UUID householdId, UUID listId,
                                @Body @Valid ShoppingListRequest request) {
        var command = new RenameShoppingListCommand(user.getName(), householdId, listId, request.name());
        return ShoppingListResponse.from(renameList.rename(command));
    }

    @Delete("/{listId}")
    HttpResponse<Void> delete(Authentication user, UUID householdId, UUID listId) {
        deleteList.delete(new ShoppingListCommand(user.getName(), householdId, listId));
        return HttpResponse.noContent();
    }

    @Post("/{listId}/clear-checked")
    HttpResponse<Void> clearChecked(Authentication user, UUID householdId, UUID listId) {
        clearChecked.clear(new ClearCheckedItemsCommand(user.getName(), householdId, listId));
        return HttpResponse.noContent();
    }
}
