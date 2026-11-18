package io.github.doriangrelu.lifemanager.bills.application.port.in.command;

import java.util.UUID;

/** Geste sur une facture existante (télécharger, supprimer). */
public record InvoiceCommand(OccurrenceCommand target, UUID invoiceId) {
}
