package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param categoryId  catégorie à renommer
 * @param name        nouveau nom
 */
public record RenameCategoryCommand(String requesterId, UUID householdId, UUID categoryId, String name) {
}
