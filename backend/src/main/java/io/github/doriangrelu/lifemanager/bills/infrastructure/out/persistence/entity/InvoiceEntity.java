package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.util.UUID;

/** Ligne de la table {@code bills.invoice}. */
@MappedEntity("invoice")
public record InvoiceEntity(@Id UUID id, UUID occurrenceId, String fileName, String mediaType, long sizeBytes,
                            UUID storageKey, UUID addedBy, Instant addedAt) {
}
