package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.util.UUID;

/** Ligne de la table {@code vehicle.maintenance_attachment}. */
@MappedEntity("maintenance_attachment")
public record MaintenanceAttachmentEntity(@Id UUID id, UUID entryId, String fileName, String mediaType,
                                          long sizeBytes, UUID storageKey, UUID addedBy, Instant addedAt) {
}
