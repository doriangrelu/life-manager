package io.github.doriangrelu.lifemanager.vehicle.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Foyer inconnu du domaine véhicule, ou dont l'utilisateur n'est pas membre (même erreur dans les deux cas). */
public class VehicleHouseholdNotFoundException extends NotFoundException {

    public VehicleHouseholdNotFoundException() {
        super("Foyer introuvable");
    }
}
