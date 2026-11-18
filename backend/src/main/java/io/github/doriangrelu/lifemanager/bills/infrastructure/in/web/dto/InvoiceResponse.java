package io.github.doriangrelu.lifemanager.bills.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.bills.application.port.in.result.InvoiceView;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/** Une facture ; {@code mediaType} est le type MIME vérifié. */
@Serdeable
public record InvoiceResponse(UUID id, String fileName, String mediaType, long sizeBytes, Instant addedAt) {

    public static InvoiceResponse from(InvoiceView view) {
        return new InvoiceResponse(view.id(), view.fileName(), view.mediaType(), view.sizeBytes(), view.addedAt());
    }
}
