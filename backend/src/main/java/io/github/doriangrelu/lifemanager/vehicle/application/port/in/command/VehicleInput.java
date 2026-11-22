package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.time.LocalDate;
/**
 * Champs de la fiche d'un véhicule tels que saisis (types simples, pas encore vérifiés).
 *
 * @param name                   nom du véhicule
 * @param licensePlate           immatriculation, ou {@code null}
 * @param technicalInspectionDue prochain contrôle technique, ou {@code null}
 * @param serviceDue             prochaine révision, ou {@code null}
 * @param mileageKm              kilométrage actuel, ou {@code null}
 * @param tireSize               taille des pneus, ou {@code null}
 */
public record VehicleInput(String name, String licensePlate, LocalDate technicalInspectionDue, LocalDate serviceDue,
                           Integer mileageKm, String tireSize) {
}
