package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Prochaines échéances d'un véhicule. Chacune est facultative et peut être déjà passée (retard).
 *
 * @param technicalInspectionDue prochain contrôle technique, ou {@code null}
 * @param serviceDue             prochaine révision, ou {@code null}
 */
public record Deadlines(LocalDate technicalInspectionDue, LocalDate serviceDue) {

    public static final Deadlines NONE = new Deadlines(null, null);

    public Optional<LocalDate> technicalInspection() {
        return Optional.ofNullable(technicalInspectionDue);
    }

    public Optional<LocalDate> service() {
        return Optional.ofNullable(serviceDue);
    }
}
