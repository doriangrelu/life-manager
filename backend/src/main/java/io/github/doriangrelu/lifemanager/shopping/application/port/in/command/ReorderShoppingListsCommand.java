package io.github.doriangrelu.lifemanager.shopping.application.port.in.command;

import java.util.List;
import java.util.UUID;

/**
 * Nouvel ordre des sous-listes d'un foyer.
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param listIds     sous-listes dans l'ordre voulu ; celles qui manquent gardent leur place après elles
 */
public record ReorderShoppingListsCommand(String requesterId, UUID householdId, List<UUID> listIds) {
}
