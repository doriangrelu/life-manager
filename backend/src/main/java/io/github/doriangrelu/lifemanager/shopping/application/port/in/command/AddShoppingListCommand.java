package io.github.doriangrelu.lifemanager.shopping.application.port.in.command;

import java.util.UUID;

/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param name        nom saisi
 */
public record AddShoppingListCommand(String requesterId, UUID householdId, String name) {
}
