package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur (auteur de la saisie)
 * @param householdId foyer concerné
 * @param vehicleId   véhicule entretenu
 * @param input       opération saisie
 */
public record RecordMaintenanceCommand(String requesterId, UUID householdId, UUID vehicleId, MaintenanceInput input) {
}
