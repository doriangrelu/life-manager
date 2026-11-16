package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param expenseId   dépense à corriger
 * @param input       nouveaux champs
 */
public record CorrectExpenseCommand(String requesterId, UUID householdId, UUID expenseId, ExpenseInput input) {
}
