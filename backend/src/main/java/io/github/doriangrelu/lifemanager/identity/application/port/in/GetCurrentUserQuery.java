package io.github.doriangrelu.lifemanager.identity.application.port.in;

/**
 * @param subject identifiant Keycloak de l'utilisateur authentifié
 */
public record GetCurrentUserQuery(String subject) {
}
