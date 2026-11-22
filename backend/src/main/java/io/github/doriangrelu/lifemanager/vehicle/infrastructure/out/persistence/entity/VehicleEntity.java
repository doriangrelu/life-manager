package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.LocalDate;
import java.util.UUID;

/** Ligne de la table {@code vehicle.vehicle}. Les champs facultatifs absents valent {@code null}. */
@MappedEntity("vehicle")
public record VehicleEntity(@Id UUID id, UUID householdId, String name, @Nullable String licensePlate,
                            @Nullable LocalDate technicalInspectionDue, @Nullable LocalDate serviceDue,
                            @Nullable Integer currentMileageKm, @Nullable String tireSize) {
}
