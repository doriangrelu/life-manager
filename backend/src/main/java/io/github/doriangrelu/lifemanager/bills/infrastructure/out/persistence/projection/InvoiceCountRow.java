package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.projection;

import io.micronaut.core.annotation.Introspected;

import java.util.UUID;

/** Nombre de factures d'une échéance. */
@Introspected
public record InvoiceCountRow(UUID occurrenceId, long total) {
}
