package io.github.doriangrelu.lifemanager.vehicle.application.port.in.query;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param vehicleId   véhicule dont on veut le carnet
 */
public record ListMaintenanceEntriesQuery(String requesterId, UUID householdId, UUID vehicleId) {
}
