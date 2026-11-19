package io.github.doriangrelu.lifemanager.identity.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.identity.application.port.in.result.UserProfile;
import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;

/** Réponse JSON de {@code GET /api/whoami} ; la PWA demande d'accepter les conditions si {@code termsAccepted} est faux. */
@Serdeable
public record UserProfileResponse(UUID userId, String email, String displayName, boolean termsAccepted,
                                  String termsVersion) {

    public static UserProfileResponse from(UserProfile profile) {
        return new UserProfileResponse(profile.userId(), profile.email(), profile.displayName(),
                profile.termsAccepted(), profile.termsVersion());
    }
}
