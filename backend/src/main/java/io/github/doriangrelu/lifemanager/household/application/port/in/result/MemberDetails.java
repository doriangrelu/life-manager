package io.github.doriangrelu.lifemanager.household.application.port.in.result;

import java.time.Instant;
import java.util.UUID;
/** Un membre d'un foyer, avec son nom d'affichage. */
public record MemberDetails(UUID memberId, String displayName, Instant joinedAt) {
}
