package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.util.UUID;

/** Ligne de la table {@code vehicle.maintenance_expense} : opération envoyée au budget. */
@MappedEntity("maintenance_expense")
public record MaintenanceExpenseEntity(@Id UUID entryId, UUID householdId, UUID categoryId, UUID payerId) {
}
