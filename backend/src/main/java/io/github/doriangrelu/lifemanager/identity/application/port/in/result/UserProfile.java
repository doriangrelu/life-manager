package io.github.doriangrelu.lifemanager.identity.application.port.in.result;

import java.util.UUID;

/**
 * Profil d'un utilisateur, tel que renvoyé à l'extérieur du domaine.
 *
 * @param termsAccepted vrai s'il a accepté la version en vigueur des conditions
 * @param termsVersion  version en vigueur des conditions
 */
public record UserProfile(UUID userId, String email, String displayName, boolean termsAccepted,
                          String termsVersion) {
}
