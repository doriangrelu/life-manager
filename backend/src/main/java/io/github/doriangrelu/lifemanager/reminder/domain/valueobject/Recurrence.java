package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.Arrays;

/** Récurrence d'un rappel saisi, en mois (0 = rappel ponctuel). */
public enum Recurrence {
    NONE(0),
    MONTHLY(1),
    QUARTERLY(3),
    YEARLY(12);

    private final int months;

    Recurrence(int months) {
        this.months = months;
    }

    public int months() {
        return months;
    }

    /** Retrouve la récurrence à partir de son nom, refuse un nom inconnu. */
    public static Recurrence of(String name) {
        var wanted = Require.notBlank(name, "La récurrence");
        return Arrays.stream(values())
                .filter(recurrence -> recurrence.name().equals(wanted))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("Récurrence inconnue : " + wanted));
    }
}
