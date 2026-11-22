package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;

/** Dépose d'une pièce d'usure, remplacée par une nouvelle : jour et kilométrage de la nouvelle pose. */
public record WearPartRemoval(LocalDate removedOn, Mileage removedKm) {

    public WearPartRemoval {
        Require.notNull(removedOn, "La date de dépose");
        Require.notNull(removedKm, "Le kilométrage à la dépose");
    }
}
