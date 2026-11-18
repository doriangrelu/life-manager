package io.github.doriangrelu.lifemanager.shopping.application.port.in.query;

import java.util.UUID;

/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param listId      sous-liste visée
 */
public record ListShoppingItemsQuery(String requesterId, UUID householdId, UUID listId) {
}
