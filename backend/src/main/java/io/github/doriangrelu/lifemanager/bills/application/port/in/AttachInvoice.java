package io.github.doriangrelu.lifemanager.bills.application.port.in;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.AttachInvoiceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.InvoiceView;

/** Joint une facture à une échéance. */
public interface AttachInvoice {

    InvoiceView attach(AttachInvoiceCommand command);
}
