package io.github.doriangrelu.lifemanager.vehicle.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Véhicule inexistant dans ce foyer. */
public class VehicleNotFoundException extends NotFoundException {

    public VehicleNotFoundException() {
        super("Véhicule introuvable");
    }
}
