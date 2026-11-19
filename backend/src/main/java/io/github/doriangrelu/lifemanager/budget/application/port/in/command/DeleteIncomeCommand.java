package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import java.util.UUID;

/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param incomeId    rentrée à supprimer
 */
public record DeleteIncomeCommand(String requesterId, UUID householdId, UUID incomeId) {
}
