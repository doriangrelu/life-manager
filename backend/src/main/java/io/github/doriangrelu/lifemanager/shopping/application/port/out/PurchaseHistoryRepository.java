package io.github.doriangrelu.lifemanager.shopping.application.port.out;

import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.PurchaseHistoryEntry;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.PurchaseHistoryEntryId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;

import java.util.List;
import java.util.Optional;

/** Stockage de l'historique des achats de chaque sous-liste. */
public interface PurchaseHistoryRepository {

    Optional<PurchaseHistoryEntry> findById(PurchaseHistoryEntryId id);

    /** Historique d'une sous-liste, du plus récemment acheté au plus ancien. */
    List<PurchaseHistoryEntry> findAllByList(ShoppingListId listId);

    /** Crée la ligne ou enregistre ses modifications. */
    void save(PurchaseHistoryEntry entry);

    void delete(PurchaseHistoryEntry entry);
}
