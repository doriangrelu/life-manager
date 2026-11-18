package io.github.doriangrelu.lifemanager.shopping.application.port.in.result;

import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingList;

import java.util.UUID;

/**
 * Une sous-liste de courses telle que vue hors du domaine.
 *
 * @param pendingCount nombre d'articles non cochés
 */
public record ShoppingListView(UUID id, String name, long pendingCount) {

    public static ShoppingListView from(ShoppingList list, long pendingCount) {
        return new ShoppingListView(list.id().value(), list.name().value(), pendingCount);
    }
}
