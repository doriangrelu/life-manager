package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.household.application.port.in.result.InvitationDetails;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
/** Un code d'invitation et sa date d'expiration. */
@Serdeable
public record InvitationResponse(String code, Instant expiresAt) {

    public static InvitationResponse from(InvitationDetails invitation) {
        return new InvitationResponse(invitation.code(), invitation.expiresAt());
    }
}
