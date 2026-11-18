package io.github.doriangrelu.lifemanager.bills.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.ConflictException;

/** Une échéance a déjà son nombre maximal de factures. */
public class TooManyInvoicesException extends ConflictException {

    public TooManyInvoicesException(int max) {
        super("Une échéance ne peut pas avoir plus de " + max + " factures");
    }
}
