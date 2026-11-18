package io.github.doriangrelu.lifemanager.bills.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ConflictException;

/** Une charge résiliée ne se modifie plus. */
public class ChargeTerminatedException extends ConflictException {

    public ChargeTerminatedException() {
        super("Cette charge est résiliée");
    }
}
