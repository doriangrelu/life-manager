package io.github.doriangrelu.lifemanager.bills.application.service;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.AttachInvoiceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.InvoiceView;
import io.github.doriangrelu.lifemanager.bills.application.port.out.InvoiceRepository;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.Invoice;
import io.github.doriangrelu.lifemanager.bills.domain.exception.TooManyInvoicesException;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.InvoiceId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachedFile;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Partie en base de l'ajout d'une facture, dans sa propre transaction (le fichier est déjà écrit). */
@Singleton
public class InvoiceRecorder {

    private final ChargeAccess access;
    private final InvoiceRepository invoices;
    private final Clock clock;

    InvoiceRecorder(ChargeAccess access, InvoiceRepository invoices, Clock clock) {
        this.access = access;
        this.invoices = invoices;
        this.clock = clock;
    }

    /** Vérifie, avant d'écrire le moindre fichier, que le demandeur voit cette échéance (sinon 404). */
    @Transactional(transactionManager = "bills", readOnly = true)
    public void checkAccess(AttachInvoiceCommand command) {
        access.occurrenceForMember(command.target());
    }

    /** @throws TooManyInvoicesException si l'échéance a déjà son maximum de factures */
    @Transactional(transactionManager = "bills")
    public InvoiceView record(AttachInvoiceCommand command, AttachedFile file) {
        var occurrence = access.occurrenceForMember(command.target());
        if (invoices.countByOccurrence(occurrence.id()) >= Invoice.MAX_PER_OCCURRENCE) {
            throw new TooManyInvoicesException(Invoice.MAX_PER_OCCURRENCE);
        }
        var invoice = Invoice.attach(InvoiceId.newId(), occurrence.id(), file,
                MemberId.of(command.target().requesterId()), clock);
        invoices.save(invoice);
        return InvoiceView.from(invoice);
    }
}
