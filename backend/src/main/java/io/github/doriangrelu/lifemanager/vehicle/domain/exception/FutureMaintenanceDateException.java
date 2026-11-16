package io.github.doriangrelu.lifemanager.vehicle.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/** Une opération d'entretien ne peut pas être datée d'un jour à venir. */
public class FutureMaintenanceDateException extends InvalidValueException {

    public FutureMaintenanceDateException() {
        super("La date d'une opération d'entretien ne peut pas être dans le futur");
    }
}
