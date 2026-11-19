package io.github.doriangrelu.lifemanager.household.application.port.in.command;

import java.util.UUID;

/**
 * Geste du propriétaire sur un membre (transmettre la propriété, retirer).
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer visé
 * @param memberId    membre visé
 */
public record MemberCommand(String requesterId, UUID householdId, UUID memberId) {
}
