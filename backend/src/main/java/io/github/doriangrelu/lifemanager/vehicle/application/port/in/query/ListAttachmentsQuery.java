package io.github.doriangrelu.lifemanager.vehicle.application.port.in.query;

import java.util.UUID;

/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param vehicleId   véhicule concerné
 * @param entryId     opération du carnet
 */
public record ListAttachmentsQuery(String requesterId, UUID householdId, UUID vehicleId, UUID entryId) {
}
