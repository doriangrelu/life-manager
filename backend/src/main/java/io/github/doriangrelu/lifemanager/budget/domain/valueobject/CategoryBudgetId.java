package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'une règle de budget prévu. */
public record CategoryBudgetId(UUID value) {

    public CategoryBudgetId {
        Require.notNull(value, "L'identifiant du budget prévu");
    }

    public static CategoryBudgetId newId() {
        return new CategoryBudgetId(UUID.randomUUID());
    }
}
