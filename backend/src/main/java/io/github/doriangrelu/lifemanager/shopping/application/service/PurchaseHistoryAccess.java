package io.github.doriangrelu.lifemanager.shopping.application.service;

import io.github.doriangrelu.lifemanager.shopping.application.port.out.PurchaseHistoryRepository;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.PurchaseHistoryEntry;
import io.github.doriangrelu.lifemanager.shopping.domain.exception.HistoryEntryNotFoundException;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.PurchaseHistoryEntryId;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Cloisonnement de l'historique : un membre n'atteint que l'historique des sous-listes de son foyer. */
@Singleton
public class PurchaseHistoryAccess {

    private final ShoppingListAccess lists;
    private final PurchaseHistoryRepository history;

    PurchaseHistoryAccess(ShoppingListAccess lists, PurchaseHistoryRepository history) {
        this.lists = lists;
        this.history = history;
    }

    /** @throws HistoryEntryNotFoundException si l'élément n'est pas dans l'historique de cette sous-liste */
    public PurchaseHistoryEntry entryForMember(UUID householdId, UUID listId, UUID entryId, String requesterId) {
        var list = lists.listForMember(householdId, listId, requesterId);
        return history.findById(new PurchaseHistoryEntryId(entryId))
                .filter(entry -> entry.belongsTo(list.id()))
                .orElseThrow(HistoryEntryNotFoundException::new);
    }
}
