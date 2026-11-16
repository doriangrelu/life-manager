package io.github.doriangrelu.lifemanager.vehicle.contract.event;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement public : un véhicule a été supprimé du foyer.
 * Contrat stable entre domaines (types simples, sérialisable en JSON pour un futur broker).
 */
@Serdeable
public record VehicleRemovedV1(UUID vehicleId, UUID householdId, Instant occurredAt) {
}
