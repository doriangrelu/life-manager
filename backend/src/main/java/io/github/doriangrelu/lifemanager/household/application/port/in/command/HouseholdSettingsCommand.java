package io.github.doriangrelu.lifemanager.household.application.port.in.command;

import java.util.UUID;

/**
 * Réglages d'un foyer.
 *
 * @param requesterId    {@code sub} Keycloak du demandeur
 * @param householdId    foyer visé
 * @param incomeTracking suivre les rentrées (bilan)
 */
public record HouseholdSettingsCommand(String requesterId, UUID householdId, boolean incomeTracking) {
}
