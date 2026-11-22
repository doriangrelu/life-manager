package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.util.UUID;

/**
 * Ajout après coup d'une opération déjà saisie au budget.
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param entryId     opération du carnet
 * @param budget      catégorie et payeur choisis
 */
public record SendToBudgetCommand(String requesterId, UUID householdId, UUID vehicleId, UUID entryId,
                                  BudgetInput budget) {
}
