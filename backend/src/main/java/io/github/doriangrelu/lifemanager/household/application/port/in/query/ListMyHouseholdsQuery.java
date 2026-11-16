package io.github.doriangrelu.lifemanager.household.application.port.in.query;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 */
public record ListMyHouseholdsQuery(String requesterId) {
}
