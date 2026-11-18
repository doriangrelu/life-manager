package io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.controller;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.ForgetHistoryEntry;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.ListPurchaseHistory;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.RestockFromHistory;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ForgetHistoryEntryCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.RestockFromHistoryCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.query.ListPurchaseHistoryQuery;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.PurchaseHistoryEntryResponse;
import io.github.doriangrelu.lifemanager.shopping.infrastructure.in.web.dto.RestockRequest;
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

/** Historique des achats d'une sous-liste : consultation, remise en liste, oubli d'un article. */
@Controller("/api/households/{householdId}/shopping-lists/{listId}/history")
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.BLOCKING)
class PurchaseHistoryController {

    private final ListPurchaseHistory listHistory;
    private final RestockFromHistory restock;
    private final ForgetHistoryEntry forget;

    PurchaseHistoryController(ListPurchaseHistory listHistory, RestockFromHistory restock,
                              ForgetHistoryEntry forget) {
        this.listHistory = listHistory;
        this.restock = restock;
        this.forget = forget;
    }

    @Get
    List<PurchaseHistoryEntryResponse> list(Authentication user, UUID householdId, UUID listId) {
        return listHistory.list(new ListPurchaseHistoryQuery(user.getName(), householdId, listId)).stream()
                .map(PurchaseHistoryEntryResponse::from)
                .toList();
    }

    @Post("/restock")
    List<ShoppingItemResponse> restock(Authentication user, UUID householdId, UUID listId,
                                       @Body @Valid RestockRequest request) {
        var command = new RestockFromHistoryCommand(user.getName(), householdId, listId, request.entryIds());
        return restock.restock(command).stream().map(ShoppingItemResponse::from).toList();
    }

    @Delete("/{entryId}")
    HttpResponse<Void> forget(Authentication user, UUID householdId, UUID listId, UUID entryId) {
        forget.forget(new ForgetHistoryEntryCommand(user.getName(), householdId, listId, entryId));
        return HttpResponse.noContent();
    }
}
