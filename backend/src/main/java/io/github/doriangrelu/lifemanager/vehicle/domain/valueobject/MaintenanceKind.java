package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.Arrays;

/** Type d'une opération d'entretien. */
public enum MaintenanceKind {
    SERVICE,
    OIL_CHANGE,
    TECHNICAL_INSPECTION,
    TIRES,
    REPAIR,
    OTHER;

    /** Retrouve le type à partir de son nom (« SERVICE »…), refuse un nom inconnu. */
    public static MaintenanceKind of(String name) {
        var wanted = Require.notBlank(name, "Le type d'entretien");
        return Arrays.stream(values())
                .filter(kind -> kind.name().equals(wanted))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("Type d'entretien inconnu : " + wanted));
    }
}
