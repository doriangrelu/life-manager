package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Ligne de la table {@code vehicle.maintenance_entry}. Coût en centimes d'euro ; champs absents à {@code null}. */
@MappedEntity("maintenance_entry")
public record MaintenanceEntryEntity(@Id UUID id, UUID vehicleId, LocalDate performedOn, String kind,
                                     @Nullable Integer mileageKm, @Nullable Long costCents, @Nullable String note,
                                     UUID recordedBy, Instant recordedAt) {
}
