package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Ce que l'utilisateur saisit sur une opération d'entretien, déjà vérifié dans sa forme.
 * Invariant : une précision n'existe que pour le type « Autre ».
 *
 * @param performedOn jour de l'opération
 * @param kind        type d'opération
 * @param mileage     kilométrage, ou {@code null}
 * @param cost        coût, ou {@code null}
 * @param note        note, ou {@code null}
 * @param otherLabel  précision du type « Autre », ou {@code null}
 */
public record MaintenanceDetails(LocalDate performedOn, MaintenanceKind kind, Mileage mileage, Cost cost,
                                 MaintenanceNote note, MaintenanceOtherLabel otherLabel) {

    public MaintenanceDetails {
        Require.notNull(performedOn, "La date de l'opération");
        Require.notNull(kind, "Le type d'entretien");
        if (otherLabel != null && kind != MaintenanceKind.OTHER) {
            throw new InvalidValueException("Seul le type « Autre » peut être précisé");
        }
    }

    /** Opération sans précision (tout type). */
    public MaintenanceDetails(LocalDate performedOn, MaintenanceKind kind, Mileage mileage, Cost cost,
                              MaintenanceNote note) {
        this(performedOn, kind, mileage, cost, note, null);
    }

    public Optional<Mileage> optionalMileage() {
        return Optional.ofNullable(mileage);
    }

    public Optional<Cost> optionalCost() {
        return Optional.ofNullable(cost);
    }

    public Optional<MaintenanceNote> optionalNote() {
        return Optional.ofNullable(note);
    }

    public Optional<MaintenanceOtherLabel> optionalOtherLabel() {
        return Optional.ofNullable(otherLabel);
    }
}
