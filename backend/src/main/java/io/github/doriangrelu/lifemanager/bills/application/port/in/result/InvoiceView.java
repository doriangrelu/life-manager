package io.github.doriangrelu.lifemanager.bills.application.port.in.result;

import io.github.doriangrelu.lifemanager.bills.domain.aggregate.Invoice;

import java.time.Instant;
import java.util.UUID;

/** Une facture vue hors du domaine ; {@code mediaType} est le type MIME vérifié. */
public record InvoiceView(UUID id, String fileName, String mediaType, long sizeBytes, Instant addedAt) {

    public static InvoiceView from(Invoice invoice) {
        var file = invoice.file();
        return new InvoiceView(invoice.id().value(), file.fileName().value(), file.mediaType().mimeType(),
                file.size().bytes(), invoice.addedAt());
    }
}
