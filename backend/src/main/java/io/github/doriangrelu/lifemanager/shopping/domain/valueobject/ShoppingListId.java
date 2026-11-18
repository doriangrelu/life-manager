package io.github.doriangrelu.lifemanager.shopping.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'une sous-liste de courses. */
public record ShoppingListId(UUID value) {

    public ShoppingListId {
        Require.notNull(value, "L'identifiant de la sous-liste");
    }

    public static ShoppingListId newId() {
        return new ShoppingListId(UUID.randomUUID());
    }
}
