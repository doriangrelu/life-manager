package io.github.doriangrelu.lifemanager.vehicle.application.port.in.query;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 */
public record ListVehiclesQuery(String requesterId, UUID householdId) {
}
