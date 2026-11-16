package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/**
 * Somme d'argent en euros, exprimée en centimes entiers (12,50 € = 1250).
 * Jamais de nombre à virgule : pas d'erreur d'arrondi, comme dans les systèmes bancaires.
 */
public record Money(long cents) implements Comparable<Money> {

    public static final Money ZERO = new Money(0);

    public Money {
        if (cents < 0) {
            throw new InvalidValueException("Un montant ne peut pas être négatif");
        }
    }

    public static Money ofCents(long cents) {
        return new Money(cents);
    }

    /** Somme des deux montants ; refuse un dépassement de capacité plutôt que de donner un faux total. */
    public Money plus(Money other) {
        return new Money(Math.addExact(cents, other.cents));
    }

    public boolean isZero() {
        return cents == 0;
    }

    @Override
    public int compareTo(Money other) {
        return Long.compare(cents, other.cents);
    }
}
