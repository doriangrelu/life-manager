package io.github.doriangrelu.lifemanager.bills.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Charge inconnue dans ce foyer. */
public class ChargeNotFoundException extends NotFoundException {

    public ChargeNotFoundException() {
        super("Charge introuvable");
    }
}
