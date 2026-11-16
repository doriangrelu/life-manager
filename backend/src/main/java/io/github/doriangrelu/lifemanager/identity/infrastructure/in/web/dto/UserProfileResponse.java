package io.github.doriangrelu.lifemanager.identity.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.identity.application.port.in.UserProfile;
import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;

/** Réponse JSON de {@code GET /api/whoami}. */
@Serdeable
public record UserProfileResponse(UUID userId, String email, String displayName) {

    public static UserProfileResponse from(UserProfile profile) {
        return new UserProfileResponse(profile.userId(), profile.email(), profile.displayName());
    }
}
