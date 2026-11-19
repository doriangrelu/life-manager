package io.github.doriangrelu.lifemanager.household.application.port.in.command;

import java.util.UUID;

/**
 * Geste d'un membre sur un foyer (quitter, annuler le code d'invitation).
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer visé
 */
public record HouseholdCommand(String requesterId, UUID householdId) {
}
