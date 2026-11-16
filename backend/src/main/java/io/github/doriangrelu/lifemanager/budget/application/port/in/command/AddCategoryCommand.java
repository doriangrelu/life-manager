package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param name        nom de la nouvelle catégorie
 */
public record AddCategoryCommand(String requesterId, UUID householdId, String name) {
}
