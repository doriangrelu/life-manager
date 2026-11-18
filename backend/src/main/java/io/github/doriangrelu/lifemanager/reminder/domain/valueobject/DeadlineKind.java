package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

import java.util.Arrays;

/**
 * Nature d'une échéance suivie, avec le nombre de jours avant l'échéance à partir duquel on la rappelle :
 * 30 jours pour un véhicule (il faut prendre rendez-vous), 7 jours pour une charge récurrente,
 * et toujours pour une échéance de charge qui attend la confirmation de son montant.
 */
public enum DeadlineKind {
    TECHNICAL_INSPECTION(30),
    SERVICE(30),
    CHARGE(7),
    CHARGE_TO_CONFIRM(Integer.MAX_VALUE);

    private final int reminderWindowDays;

    DeadlineKind(int reminderWindowDays) {
        this.reminderWindowDays = reminderWindowDays;
    }

    public int reminderWindowDays() {
        return reminderWindowDays;
    }

    /** Retrouve la nature à partir de son nom, refuse un nom inconnu. */
    public static DeadlineKind of(String name) {
        return Arrays.stream(values())
                .filter(kind -> kind.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("Type d'échéance inconnu : " + name));
    }
}
