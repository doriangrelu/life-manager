package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'une dépense. */
public record ExpenseId(UUID value) {

    private static final String FIELD = "L'identifiant de la dépense";

    public ExpenseId {
        Require.notNull(value, FIELD);
    }

    public static ExpenseId newId() {
        return new ExpenseId(UUID.randomUUID());
    }
}
