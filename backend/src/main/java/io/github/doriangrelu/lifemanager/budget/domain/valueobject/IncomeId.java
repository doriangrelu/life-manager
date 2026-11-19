package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'une rentrée d'argent. */
public record IncomeId(UUID value) {

    public IncomeId {
        Require.notNull(value, "L'identifiant de la rentrée");
    }

    public static IncomeId newId() {
        return new IncomeId(UUID.randomUUID());
    }
}
