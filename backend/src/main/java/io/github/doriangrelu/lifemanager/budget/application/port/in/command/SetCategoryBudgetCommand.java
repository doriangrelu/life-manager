package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import java.time.YearMonth;
import java.util.UUID;

/**
 * @param requesterId  {@code sub} Keycloak du demandeur
 * @param householdId  foyer concerné
 * @param categoryId   catégorie de dépenses visée
 * @param month        mois de départ (ou seul mois concerné)
 * @param scope        « FROM_MONTH » ou « MONTH_ONLY »
 * @param plannedCents montant prévu en centimes, ou {@code null} pour retirer le prévu
 */
public record SetCategoryBudgetCommand(String requesterId, UUID householdId, UUID categoryId, YearMonth month,
                                       String scope, Long plannedCents) {
}
