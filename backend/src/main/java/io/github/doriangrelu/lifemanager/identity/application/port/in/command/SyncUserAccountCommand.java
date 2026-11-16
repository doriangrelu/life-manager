package io.github.doriangrelu.lifemanager.identity.application.port.in.command;

/**
 * Données d'identité confirmées par Keycloak.
 *
 * @param subject     identifiant Keycloak ({@code sub})
 * @param email       email actuel
 * @param displayName nom d'affichage actuel
 */
public record SyncUserAccountCommand(String subject, String email, String displayName) {
}
