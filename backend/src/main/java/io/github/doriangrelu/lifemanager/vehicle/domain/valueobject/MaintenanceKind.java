package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Period;
import java.util.Arrays;
import java.util.Optional;

/** Type d'une opération d'entretien. */
public enum MaintenanceKind {
    SERVICE(Period.ofYears(1), "Révision"),
    OIL_CHANGE(null, "Vidange"),
    TECHNICAL_INSPECTION(Period.ofYears(2), "Contrôle technique"),
    TIRES(null, "Pneus"),
    REPAIR(null, "Réparation"),
    OTHER(null, "Entretien");

    /** Durée avant la prochaine opération du même type, ou {@code null} si elle ne fixe pas d'échéance. */
    private final Period validity;
    /** Nom affiché (libellé de la dépense dans le budget). */
    private final String label;

    MaintenanceKind(Period validity, String label) {
        this.validity = validity;
        this.label = label;
    }

    public String label() {
        return label;
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
