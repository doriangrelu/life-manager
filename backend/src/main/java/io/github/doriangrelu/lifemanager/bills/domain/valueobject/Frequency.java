package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

import java.util.Arrays;

/** Rythme d'une charge, en nombre de mois entre deux échéances. */
public enum Frequency {
    MONTHLY(1),
    BIMONTHLY(2),
    QUARTERLY(3),
    YEARLY(12);

    private final int months;

    Frequency(int months) {
        this.months = months;
    }

    public int months() {
        return months;
    }

    public static Frequency of(String name) {
        return Arrays.stream(values())
                .filter(frequency -> frequency.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("Fréquence inconnue : " + name));
    }
}
