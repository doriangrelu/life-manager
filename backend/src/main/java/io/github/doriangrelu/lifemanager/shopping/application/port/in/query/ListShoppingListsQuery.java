package io.github.doriangrelu.lifemanager.shopping.application.port.in.query;

import java.util.UUID;

/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 */
public record ListShoppingListsQuery(String requesterId, UUID householdId) {
}
