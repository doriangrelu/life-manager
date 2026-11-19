package io.github.doriangrelu.lifemanager.budget.application.port.in.query;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param kind        catégories de dépenses ou de rentrées
 */
public record ListCategoriesQuery(String requesterId, UUID householdId, CategoryKind kind) {
}
