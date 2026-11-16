package io.github.doriangrelu.lifemanager.household.application.port.in.result;

import java.time.Instant;
/** Code d'invitation à transmettre, et sa date d'expiration. */
public record InvitationDetails(String code, Instant expiresAt) {
}
