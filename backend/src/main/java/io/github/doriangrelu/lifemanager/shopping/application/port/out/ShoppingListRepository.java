package io.github.doriangrelu.lifemanager.shopping.application.port.out;

import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingList;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ListName;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;

import java.util.List;
import java.util.Optional;

/** Stockage des sous-listes de courses. */
public interface ShoppingListRepository {

    Optional<ShoppingList> findById(ShoppingListId id);

    /** Sous-listes du foyer, dans l'ordre choisi par le foyer (une nouvelle arrive en dernier). */
    List<ShoppingList> findAllByHousehold(HouseholdId householdId);

    /**
     * Range les sous-listes du foyer : celles données d'abord, dans cet ordre, puis les autres à leur place relative.
     * Tous les identifiants donnés appartiennent au foyer (vérifié avant).
     */
    void reorder(HouseholdId householdId, List<ShoppingListId> ordered);

    /** Sous-liste du foyer portant ce nom, sans tenir compte des majuscules. */
    Optional<ShoppingList> findByName(HouseholdId householdId, ListName name);

    boolean existsForHousehold(HouseholdId householdId);

    /** Crée la sous-liste ou enregistre ses modifications. */
    void save(ShoppingList list);

    /** Supprime la sous-liste avec ses articles et son historique. */
    void delete(ShoppingList list);
}
