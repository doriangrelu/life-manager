package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.time.LocalDate;
/**
 * Champs d'une opération d'entretien tels que saisis (types simples, pas encore vérifiés).
 *
 * @param performedOn jour de l'opération
 * @param kind        type (« SERVICE », « OIL_CHANGE »…)
 * @param mileageKm   kilométrage, ou {@code null}
 * @param costCents   coût en centimes d'euro, ou {@code null}
 * @param note        note, ou {@code null}
 */
public record MaintenanceInput(LocalDate performedOn, String kind, Integer mileageKm, Long costCents, String note) {
}
