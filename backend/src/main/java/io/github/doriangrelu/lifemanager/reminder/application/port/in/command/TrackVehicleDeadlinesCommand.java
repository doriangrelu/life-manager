package io.github.doriangrelu.lifemanager.reminder.application.port.in.command;

import java.time.LocalDate;
import java.util.UUID;
/**
 * État actuel des échéances d'un véhicule, tel que publié par le domaine véhicule.
 *
 * @param technicalInspectionDue prochain contrôle technique, ou {@code null}
 * @param serviceDue             prochaine révision, ou {@code null}
 */
public record TrackVehicleDeadlinesCommand(UUID vehicleId, UUID householdId, String vehicleName,
                                           LocalDate technicalInspectionDue, LocalDate serviceDue) {
}
