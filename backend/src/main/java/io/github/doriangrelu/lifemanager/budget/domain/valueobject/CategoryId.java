package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'une catégorie de dépenses. */
public record CategoryId(UUID value) {

    private static final String FIELD = "L'identifiant de la catégorie";

    public CategoryId {
        Require.notNull(value, FIELD);
    }

    public static CategoryId newId() {
        return new CategoryId(UUID.randomUUID());
    }
}
