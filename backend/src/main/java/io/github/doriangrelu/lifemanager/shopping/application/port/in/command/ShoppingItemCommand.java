package io.github.doriangrelu.lifemanager.shopping.application.port.in.command;

import java.util.UUID;
/**
 * Geste sur un article existant (cocher, décocher, supprimer).
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param itemId      article visé
 */
public record ShoppingItemCommand(String requesterId, UUID householdId, UUID itemId) {
}
