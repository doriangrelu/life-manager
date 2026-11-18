package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.ListInvoices;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.OccurrenceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.InvoiceView;
import io.github.doriangrelu.lifemanager.bills.application.port.out.InvoiceRepository;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Liste les factures d'une échéance. */
@Singleton
class ListInvoicesUseCase implements ListInvoices {

    private final ChargeAccess access;
    private final InvoiceRepository invoices;

    ListInvoicesUseCase(ChargeAccess access, InvoiceRepository invoices) {
        this.access = access;
        this.invoices = invoices;
    }

    @Override
    @Transactional(transactionManager = "bills", readOnly = true)
    public List<InvoiceView> list(OccurrenceCommand command) {
        var occurrence = access.occurrenceForMember(command);
        return invoices.findAllByOccurrence(occurrence.id()).stream().map(InvoiceView::from).toList();
    }
}
