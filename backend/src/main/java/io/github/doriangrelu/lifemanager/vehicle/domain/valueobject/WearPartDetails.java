package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;

/**
 * Pose d'une pièce d'usure, déjà vérifiée dans sa forme.
 *
 * @param label          marque ou modèle (60 caractères au plus), ou {@code null}
 * @param fittedOn       jour de la pose
 * @param fittedKm       kilométrage à la pose
 * @param replaceAfterKm à changer tous les … km (1 à 500 000), ou {@code null}
 */
public record WearPartDetails(String label, LocalDate fittedOn, Mileage fittedKm, Integer replaceAfterKm) {

    private static final int MAX_LABEL = 60;
    private static final int MAX_INTERVAL = 500_000;

    public WearPartDetails {
        label = label == null || label.isBlank() ? null
                : Require.maxLength(label.trim().replaceAll("\\s+", " "), MAX_LABEL, "La marque ou le modèle");
        Require.notNull(fittedOn, "La date de pose");
        Require.notNull(fittedKm, "Le kilométrage à la pose");
        if (replaceAfterKm != null && (replaceAfterKm < 1 || replaceAfterKm > MAX_INTERVAL)) {
            throw new InvalidValueException("L'intervalle de remplacement doit être compris entre 1 et 500 000 km");
        }
    }
}
