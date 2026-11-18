package io.github.doriangrelu.lifemanager.bills.application.port.in;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.OccurrenceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.InvoiceView;
import java.util.List;

/** Liste les factures d'une échéance. */
public interface ListInvoices {

    List<InvoiceView> list(OccurrenceCommand command);
}
