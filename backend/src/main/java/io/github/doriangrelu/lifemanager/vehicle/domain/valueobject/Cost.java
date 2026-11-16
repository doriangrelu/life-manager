package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/** Coût d'une opération d'entretien en centimes d'euro (189,90 € = 18990) : 0 à 100 000 €. */
public record Cost(long cents) {

    private static final long MAX_CENTS = 10_000_000L;

    public Cost {
        if (cents < 0 || cents > MAX_CENTS) {
            throw new InvalidValueException("Le coût doit être compris entre 0 € et 100 000 €");
        }
    }

    /** Le coût est facultatif : {@code null} donne {@code null}. */
    public static Cost ofNullable(Long cents) {
        return cents == null ? null : new Cost(cents);
    }
}
