package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/** Montant prévu pour une catégorie sur un mois, en centimes : de 0,01 € à 1 000 000 €. */
public record PlannedAmount(long cents) {

    private static final long MAX_CENTS = 100_000_000L;

    public PlannedAmount {
        if (cents < 1 || cents > MAX_CENTS) {
            throw new InvalidValueException("Le montant prévu doit être compris entre 0,01 € et 1 000 000 €");
        }
    }

    /** Un montant absent veut dire « pas de prévu » : il donne {@code null}. */
    public static PlannedAmount ofNullable(Long cents) {
        return cents == null ? null : new PlannedAmount(cents);
    }
}
