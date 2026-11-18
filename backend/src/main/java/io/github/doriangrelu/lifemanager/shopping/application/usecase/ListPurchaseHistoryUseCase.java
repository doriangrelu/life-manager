package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.ListPurchaseHistory;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.query.ListPurchaseHistoryQuery;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.PurchaseHistoryEntryView;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.PurchaseHistoryRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Donne l'historique des achats d'une sous-liste à un membre du foyer. */
@Singleton
class ListPurchaseHistoryUseCase implements ListPurchaseHistory {

    private final ShoppingListAccess access;
    private final PurchaseHistoryRepository history;

    ListPurchaseHistoryUseCase(ShoppingListAccess access, PurchaseHistoryRepository history) {
        this.access = access;
        this.history = history;
    }

    @Override
    @Transactional(transactionManager = "shopping", readOnly = true)
    public List<PurchaseHistoryEntryView> list(ListPurchaseHistoryQuery query) {
        var list = access.listForMember(query.householdId(), query.listId(), query.requesterId());
        return history.findAllByList(list.id()).stream().map(PurchaseHistoryEntryView::from).toList();
    }
}
