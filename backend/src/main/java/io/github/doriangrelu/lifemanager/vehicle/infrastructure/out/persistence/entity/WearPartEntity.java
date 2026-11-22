package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.LocalDate;
import java.util.UUID;

/** Ligne de la table {@code vehicle.wear_part} ; dépose vide = pièce montée. */
@MappedEntity("wear_part")
public record WearPartEntity(@Id UUID id, UUID vehicleId, String kind, @Nullable String label, LocalDate fittedOn,
                             int fittedKm, @Nullable Integer replaceAfterKm, @Nullable LocalDate removedOn,
                             @Nullable Integer removedKm) {
}
