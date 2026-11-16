package io.github.doriangrelu.lifemanager.shopping.application.port.out;

import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingItemId;

import java.util.List;
import java.util.Optional;
/** Stockage des articles de courses. */
public interface ShoppingItemRepository {

    Optional<ShoppingItem> findById(ShoppingItemId id);

    /** Liste du foyer : non cochés du plus ancien au plus récent, puis cochés du dernier coché au premier. */
    List<ShoppingItem> findAllByHousehold(HouseholdId householdId);

    /** Crée l'article ou enregistre ses modifications. */
    void save(ShoppingItem item);

    void delete(ShoppingItem item);

    /** Supprime tous les articles cochés du foyer. */
    void deleteChecked(HouseholdId householdId);
}
