package io.github.doriangrelu.lifemanager.household.application.port.in.command;

import java.util.UUID;

/**
 * Suppression d'un foyer, confirmée par son nom.
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer visé
 * @param confirmName nom du foyer retapé par le demandeur
 */
public record DeleteHouseholdCommand(String requesterId, UUID householdId, String confirmName) {
}
