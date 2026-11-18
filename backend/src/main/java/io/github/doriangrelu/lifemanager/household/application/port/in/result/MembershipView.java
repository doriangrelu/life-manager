package io.github.doriangrelu.lifemanager.household.application.port.in.result;

import java.util.UUID;

/** Un membre d'un foyer. */
public record MembershipView(UUID householdId, UUID memberId) {
}
