package io.github.doriangrelu.lifemanager.shopping.application.port.in.command;

import java.util.UUID;

/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param listId      sous-liste visée
 * @param label       libellé saisi
 */
public record AddShoppingItemCommand(String requesterId, UUID householdId, UUID listId, String label) {
}
