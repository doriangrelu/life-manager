package io.github.doriangrelu.lifemanager.shopping.application.service;

import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingListRepository;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingList;
import io.github.doriangrelu.lifemanager.shopping.domain.exception.DuplicateShoppingListNameException;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ListName;
import jakarta.inject.Singleton;

/**
 * Unicité du nom d'une sous-liste dans son foyer, sans tenir compte des majuscules (ajout et renommage).
 * L'index unique en base reste le dernier rempart en cas de saisies simultanées.
 */
@Singleton
public class ShoppingListNames {

    private final ShoppingListRepository lists;

    ShoppingListNames(ShoppingListRepository lists) {
        this.lists = lists;
    }

    /** Nom d'une nouvelle sous-liste. @throws DuplicateShoppingListNameException s'il est déjà pris */
    public ListName freeName(HouseholdId household, String name) {
        var listName = new ListName(name);
        if (lists.findByName(household, listName).isPresent()) {
            throw new DuplicateShoppingListNameException();
        }
        return listName;
    }

    /** Nouveau nom d'une sous-liste ; garder son nom (même casse changée) est permis. */
    public ListName freeNameFor(ShoppingList renamed, String name) {
        var listName = new ListName(name);
        if (lists.findByName(renamed.householdId(), listName).filter(other -> !other.id().equals(renamed.id())).isPresent()) {
            throw new DuplicateShoppingListNameException();
        }
        return listName;
    }
}
