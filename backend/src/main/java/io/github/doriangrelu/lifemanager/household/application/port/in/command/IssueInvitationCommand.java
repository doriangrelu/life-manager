package io.github.doriangrelu.lifemanager.household.application.port.in.command;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer pour lequel générer le code
 */
public record IssueInvitationCommand(String requesterId, UUID householdId) {
}
