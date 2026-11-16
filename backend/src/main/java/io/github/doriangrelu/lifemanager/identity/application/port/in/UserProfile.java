package io.github.doriangrelu.lifemanager.identity.application.port.in;

import java.util.UUID;

/**
 * Profil d'un utilisateur, tel que renvoyé à l'extérieur du domaine.
 */
public record UserProfile(UUID userId, String email, String displayName) {
}
