package io.github.doriangrelu.lifemanager.bills.application.port.in.command;

/** Facture à joindre ; {@code content} est un simple transport (10 Mo au plus), jamais comparé. */
public record AttachInvoiceCommand(OccurrenceCommand target, String fileName, byte[] content) {
}
