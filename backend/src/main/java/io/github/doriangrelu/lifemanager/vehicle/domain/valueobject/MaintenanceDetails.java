package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Ce que l'utilisateur saisit sur une opération d'entretien, déjà vérifié dans sa forme.
 *
 * @param performedOn jour de l'opération
 * @param kind        type d'opération
 * @param mileage     kilométrage, ou {@code null}
 * @param cost        coût, ou {@code null}
 * @param note        note, ou {@code null}
 */
public record MaintenanceDetails(LocalDate performedOn, MaintenanceKind kind, Mileage mileage, Cost cost,
                                 MaintenanceNote note) {

    public MaintenanceDetails {
        Require.notNull(performedOn, "La date de l'opération");
        Require.notNull(kind, "Le type d'entretien");
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
}
