package io.github.doriangrelu.lifemanager.platform.infrastructure.security.event;

/**
 * Publié quand Keycloak vient de confirmer l'identité d'un utilisateur (hors cache).
 * Permet au domaine identité de tenir le profil local à jour, sans que la plateforme le connaisse.
 *
 * @param subject     identifiant Keycloak ({@code sub})
 * @param email       email actuel
 * @param displayName nom d'affichage actuel
 */
public record UserAuthenticated(String subject, String email, String displayName) {
}
