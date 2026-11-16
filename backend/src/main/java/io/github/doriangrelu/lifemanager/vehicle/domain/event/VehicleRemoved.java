package io.github.doriangrelu.lifemanager.vehicle.domain.event;

import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;

import java.time.Instant;

/** Un véhicule a été supprimé du foyer, avec son carnet. */
public record VehicleRemoved(VehicleId vehicleId, HouseholdId householdId, Instant occurredAt) implements DomainEvent {
}
