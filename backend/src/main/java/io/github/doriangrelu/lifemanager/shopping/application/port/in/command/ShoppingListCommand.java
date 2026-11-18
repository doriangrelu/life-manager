package io.github.doriangrelu.lifemanager.shopping.application.port.in.command;

import java.util.UUID;

/**
 * Geste sur une sous-liste existante (suppression).
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param listId      sous-liste visée
 */
public record ShoppingListCommand(String requesterId, UUID householdId, UUID listId) {
}
