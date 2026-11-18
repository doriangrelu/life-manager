package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.DownloadInvoice;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.InvoiceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.InvoiceContent;
import io.github.doriangrelu.lifemanager.bills.application.port.out.InvoiceStorage;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Donne le contenu d'une facture à un membre du foyer. */
@Singleton
class DownloadInvoiceUseCase implements DownloadInvoice {

    private final ChargeAccess access;
    private final InvoiceStorage storage;

    DownloadInvoiceUseCase(ChargeAccess access, InvoiceStorage storage) {
        this.access = access;
        this.storage = storage;
    }

    @Override
    @Transactional(transactionManager = "bills", readOnly = true)
    public InvoiceContent download(InvoiceCommand command) {
        var file = access.invoiceForMember(command).file();
        return new InvoiceContent(file.fileName().value(), file.mediaType().mimeType(), file.size().bytes(),
                storage.open(file.storageKey()));
    }
}
