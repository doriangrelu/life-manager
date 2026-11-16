package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param vehicleId   véhicule du carnet
 * @param entryId     opération à supprimer
 */
public record DeleteMaintenanceEntryCommand(String requesterId, UUID householdId, UUID vehicleId, UUID entryId) {
}
