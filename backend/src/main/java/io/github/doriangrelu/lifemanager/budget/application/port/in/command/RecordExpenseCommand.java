package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur (auteur de la saisie)
 * @param householdId foyer concerné
 * @param input       champs saisis
 */
public record RecordExpenseCommand(String requesterId, UUID householdId, ExpenseInput input) {
}
