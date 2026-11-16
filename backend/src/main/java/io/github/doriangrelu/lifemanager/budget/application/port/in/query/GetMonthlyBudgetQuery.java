package io.github.doriangrelu.lifemanager.budget.application.port.in.query;

import java.time.YearMonth;
import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param month       mois demandé
 */
public record GetMonthlyBudgetQuery(String requesterId, UUID householdId, YearMonth month) {
}
