package io.github.doriangrelu.lifemanager.vehicle.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ConflictException;

/** Une opération du carnet a déjà son nombre maximal de pièces jointes. */
public class TooManyAttachmentsException extends ConflictException {

    public TooManyAttachmentsException(int max) {
        super("Une opération ne peut pas avoir plus de " + max + " pièces jointes");
    }
}
