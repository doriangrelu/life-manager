package io.github.doriangrelu.lifemanager.bills.application.port.in;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.InvoiceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.InvoiceContent;

/** Donne le contenu d'une facture. */
public interface DownloadInvoice {

    InvoiceContent download(InvoiceCommand command);
}
