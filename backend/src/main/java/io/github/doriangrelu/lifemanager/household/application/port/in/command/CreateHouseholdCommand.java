package io.github.doriangrelu.lifemanager.household.application.port.in.command;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param name        nom souhaité pour le foyer
 */
public record CreateHouseholdCommand(String requesterId, String name) {
}
