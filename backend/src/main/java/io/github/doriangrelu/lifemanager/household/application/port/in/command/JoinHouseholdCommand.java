package io.github.doriangrelu.lifemanager.household.application.port.in.command;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param code        code d'invitation saisi
 */
public record JoinHouseholdCommand(String requesterId, String code) {
}
