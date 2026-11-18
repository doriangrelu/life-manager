package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.RemoveInvoice;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.InvoiceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.out.InvoiceRepository;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeAccess;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Supprime une facture ; son fichier quitte le disque une fois la suppression validée. */
@Singleton
class RemoveInvoiceUseCase implements RemoveInvoice {

    private final ChargeAccess access;
    private final InvoiceRepository invoices;
    private final DomainEventPublisher events;
    private final Clock clock;

    RemoveInvoiceUseCase(ChargeAccess access, InvoiceRepository invoices, DomainEventPublisher events, Clock clock) {
        this.access = access;
        this.invoices = invoices;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "bills")
    public void remove(InvoiceCommand command) {
        var invoice = access.invoiceForMember(command);
        invoice.markRemoved(clock);
        invoices.delete(invoice);
        events.publishEventsOf(invoice);
    }
}
