package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param name        nom de la nouvelle catégorie
 * @param kind        catégorie de dépenses ou de rentrées
 */
public record AddCategoryCommand(String requesterId, UUID householdId, String name, CategoryKind kind) {
}
