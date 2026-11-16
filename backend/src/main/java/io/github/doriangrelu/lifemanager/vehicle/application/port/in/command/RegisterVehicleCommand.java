package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param input       fiche saisie
 */
public record RegisterVehicleCommand(String requesterId, UUID householdId, VehicleInput input) {
}
