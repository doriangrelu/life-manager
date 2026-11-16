package io.github.doriangrelu.lifemanager.budget.application.port.in.query;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 */
public record ListCategoriesQuery(String requesterId, UUID householdId) {
}
