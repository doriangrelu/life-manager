package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

import java.util.Arrays;

/** Nature d'une échéance suivie. */
public enum DeadlineKind {
    TECHNICAL_INSPECTION,
    SERVICE;

    /** Retrouve la nature à partir de son nom, refuse un nom inconnu. */
    public static DeadlineKind of(String name) {
        return Arrays.stream(values())
                .filter(kind -> kind.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("Type d'échéance inconnu : " + name));
    }
}
