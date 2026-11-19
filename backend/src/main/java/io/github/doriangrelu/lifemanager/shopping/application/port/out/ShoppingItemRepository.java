package io.github.doriangrelu.lifemanager.shopping.application.port.out;

import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingItemId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Stockage des articles de courses. */
public interface ShoppingItemRepository {

    Optional<ShoppingItem> findById(ShoppingItemId id);

    /** Articles d'une sous-liste : non cochés dans l'ordre choisi par le foyer, puis cochés du dernier coché au premier. */
    List<ShoppingItem> findAllByList(ShoppingListId listId);

    /**
     * Range les articles de la sous-liste : ceux donnés d'abord, dans cet ordre, puis les autres à leur place relative.
     * Tous les identifiants donnés appartiennent à la sous-liste (vérifié avant).
     */
    void reorder(ShoppingListId listId, List<ShoppingItemId> ordered);

    List<ShoppingItem> findCheckedByList(ShoppingListId listId);

    /** Nombre d'articles non cochés de chaque sous-liste du foyer ; une sous-liste sans article est absente. */
    Map<ShoppingListId, Long> countUncheckedByList(HouseholdId householdId);

    /** Crée l'article ou enregistre ses modifications. */
    void save(ShoppingItem item);

    void delete(ShoppingItem item);

    /** Supprime tous les articles cochés de la sous-liste. */
    void deleteChecked(ShoppingListId listId);
}
