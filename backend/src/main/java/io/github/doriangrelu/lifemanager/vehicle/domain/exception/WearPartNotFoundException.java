package io.github.doriangrelu.lifemanager.vehicle.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Pièce d'usure inexistante pour ce véhicule. */
public class WearPartNotFoundException extends NotFoundException {

    public WearPartNotFoundException() {
        super("Pièce d'usure introuvable");
    }
}
