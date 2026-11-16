package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param vehicleId   véhicule à modifier
 * @param input       nouvelle fiche
 */
public record UpdateVehicleCommand(String requesterId, UUID householdId, UUID vehicleId, VehicleInput input) {
}
