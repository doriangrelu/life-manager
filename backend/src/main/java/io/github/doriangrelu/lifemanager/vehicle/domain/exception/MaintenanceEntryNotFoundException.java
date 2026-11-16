package io.github.doriangrelu.lifemanager.vehicle.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Opération d'entretien inexistante dans le carnet de ce véhicule. */
public class MaintenanceEntryNotFoundException extends NotFoundException {

    public MaintenanceEntryNotFoundException() {
        super("Opération d'entretien introuvable");
    }
}
