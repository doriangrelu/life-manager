package io.github.doriangrelu.lifemanager.bills.application.port.in;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.InvoiceCommand;

/** Supprime une facture. */
public interface RemoveInvoice {

    void remove(InvoiceCommand command);
}
