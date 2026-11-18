package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

import java.util.Arrays;

/** Montant connu d'avance (abonnement) ou à confirmer à chaque échéance (électricité, eau…). */
public enum AmountKind {
    FIXED,
    VARIABLE;

    public static AmountKind of(String name) {
        return Arrays.stream(values())
                .filter(kind -> kind.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("Type de montant inconnu : " + name));
    }
}
