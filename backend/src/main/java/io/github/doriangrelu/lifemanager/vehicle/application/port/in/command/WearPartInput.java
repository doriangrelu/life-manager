package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.time.LocalDate;

/**
 * Pose d'une pièce d'usure telle que saisie (types simples, pas encore vérifiés).
 *
 * @param kind           type (« TIRES_FRONT »…) ; ignoré pour une correction
 * @param label          marque ou modèle, ou {@code null}
 * @param fittedKm       kilométrage à la pose
 * @param replaceAfterKm à changer tous les … km, ou {@code null}
 */
public record WearPartInput(String kind, String label, LocalDate fittedOn, Integer fittedKm,
                            Integer replaceAfterKm) {
}
