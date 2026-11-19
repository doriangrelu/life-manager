package io.github.doriangrelu.lifemanager.shopping.application.port.in.command;

import java.util.List;
import java.util.UUID;

/**
 * Nouvel ordre des articles d'une sous-liste.
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param listId      sous-liste visée
 * @param itemIds     articles dans l'ordre voulu ; ceux qui manquent gardent leur place après eux
 */
public record ReorderShoppingItemsCommand(String requesterId, UUID householdId, UUID listId, List<UUID> itemIds) {
}
