package io.github.doriangrelu.lifemanager.household.application.port.in.query;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer demandé
 */
public record GetHouseholdQuery(String requesterId, UUID householdId) {
}
