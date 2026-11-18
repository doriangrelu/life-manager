package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/** Montant d'une charge en centimes d'euro : de 0 à 100 000 € (0 = mois offert). */
public record Amount(long cents) {

    /** Plafond : 100 000 €. */
    public static final long MAX_CENTS = 10_000_000L;

    public Amount {
        if (cents < 0 || cents > MAX_CENTS) {
            throw new InvalidValueException("Le montant doit être compris entre 0 € et 100 000 €");
        }
    }

    public static Amount ofCents(long cents) {
        return new Amount(cents);
    }

    public boolean isZero() {
        return cents == 0;
    }
}
