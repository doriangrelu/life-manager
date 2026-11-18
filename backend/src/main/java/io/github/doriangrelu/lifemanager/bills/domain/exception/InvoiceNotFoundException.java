package io.github.doriangrelu.lifemanager.bills.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Facture inconnue pour cette échéance. */
public class InvoiceNotFoundException extends NotFoundException {

    public InvoiceNotFoundException() {
        super("Facture introuvable");
    }
}
