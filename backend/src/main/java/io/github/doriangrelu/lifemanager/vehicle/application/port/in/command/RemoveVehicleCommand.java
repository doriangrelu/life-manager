package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param vehicleId   véhicule à supprimer
 */
public record RemoveVehicleCommand(String requesterId, UUID householdId, UUID vehicleId) {
}
