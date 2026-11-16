package io.github.doriangrelu.lifemanager.shopping.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant de l'article. */
public record ShoppingItemId(UUID value) {

    private static final String FIELD = "L'identifiant de l'article";

    public ShoppingItemId {
        Require.notNull(value, FIELD);
    }

    public static ShoppingItemId newId() {
        return new ShoppingItemId(UUID.randomUUID());
    }
}
