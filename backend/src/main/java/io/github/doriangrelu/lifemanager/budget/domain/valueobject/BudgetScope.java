package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.Arrays;

/** Portée d'un budget prévu : à partir d'un mois (« tous les mois ») ou pour ce mois seulement. */
public enum BudgetScope {
    FROM_MONTH,
    MONTH_ONLY;

    /** Retrouve la portée à partir de son nom, refuse un nom inconnu. */
    public static BudgetScope of(String name) {
        var wanted = Require.notBlank(name, "La portée du budget prévu");
        return Arrays.stream(values())
                .filter(scope -> scope.name().equals(wanted))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("Portée de budget inconnue : " + wanted));
    }
}
