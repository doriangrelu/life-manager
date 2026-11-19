package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

import java.util.Arrays;

/** Sens d'une charge récurrente : argent qui sort (dépense) ou qui rentre (salaire, aide…). */
public enum FlowDirection {
    EXPENSE,
    INCOME;

    /** Sens donné par son nom ; absent = dépense (comportement d'avant les rentrées). */
    public static FlowDirection of(String name) {
        if (name == null || name.isBlank()) {
            return EXPENSE;
        }
        return Arrays.stream(values())
                .filter(direction -> direction.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("Sens inconnu : " + name));
    }
}
