package io.github.doriangrelu.lifemanager.vehicle.domain.event;

import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;

import java.time.Instant;

/** Un véhicule a été ajouté au foyer. */
public record VehicleRegistered(VehicleId vehicleId, HouseholdId householdId, VehicleDetails details,
                                Instant occurredAt) implements DomainEvent {
}
