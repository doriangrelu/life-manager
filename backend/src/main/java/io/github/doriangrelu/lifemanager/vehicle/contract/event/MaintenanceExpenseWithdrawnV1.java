package io.github.doriangrelu.lifemanager.vehicle.contract.event;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement public : une opération ajoutée au budget a été supprimée ; sa dépense doit être supprimée.
 * Contrat stable entre domaines (types simples, sérialisable en JSON).
 */
@Serdeable
public record MaintenanceExpenseWithdrawnV1(UUID entryId, UUID householdId, Instant occurredAt) {
}
