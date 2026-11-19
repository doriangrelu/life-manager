package io.github.doriangrelu.lifemanager.household.application.port.in.command;

import java.util.UUID;

/**
 * Nouveau nom d'un foyer.
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer visé
 * @param name        nouveau nom
 */
public record RenameHouseholdCommand(String requesterId, UUID householdId, String name) {
}
