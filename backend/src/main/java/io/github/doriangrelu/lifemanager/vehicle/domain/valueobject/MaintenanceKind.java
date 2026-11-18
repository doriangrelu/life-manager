package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Period;
import java.util.Arrays;
import java.util.Optional;

/** Type d'une opération d'entretien. */
public enum MaintenanceKind {
    SERVICE(Period.ofYears(1)),
    OIL_CHANGE(null),
    TECHNICAL_INSPECTION(Period.ofYears(2)),
    TIRES(null),
    REPAIR(null),
    OTHER(null);

    /** Durée avant la prochaine opération du même type, ou {@code null} si elle ne fixe pas d'échéance. */
    private final Period validity;

    MaintenanceKind(Period validity) {
        this.validity = validity;
    }

    /** Retrouve le type à partir de son nom (« SERVICE »…), refuse un nom inconnu. */
    public static MaintenanceKind of(String name) {
        var wanted = Require.notBlank(name, "Le type d'entretien");
        return Arrays.stream(values())
                .filter(kind -> kind.name().equals(wanted))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("Type d'entretien inconnu : " + wanted));
    }

    /** Contrôle technique : 2 ans ; révision : 1 an ; vide pour un type qui ne fixe pas d'échéance. */
    public Optional<Period> validity() {
        return Optional.ofNullable(validity);
    }
}
