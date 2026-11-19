package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.Arrays;

/** À partir de quand prévenir avant la date d'un rappel saisi : le jour même, 1 semaine ou 1 mois avant. */
public enum NoticePeriod {
    SAME_DAY(0),
    ONE_WEEK(7),
    ONE_MONTH(30);

    private final int days;

    NoticePeriod(int days) {
        this.days = days;
    }

    public int days() {
        return days;
    }

    /** Retrouve le délai à partir de son nom, refuse un nom inconnu. */
    public static NoticePeriod of(String name) {
        var wanted = Require.notBlank(name, "Le délai de rappel");
        return Arrays.stream(values())
                .filter(notice -> notice.name().equals(wanted))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("Délai de rappel inconnu : " + wanted));
    }
}
