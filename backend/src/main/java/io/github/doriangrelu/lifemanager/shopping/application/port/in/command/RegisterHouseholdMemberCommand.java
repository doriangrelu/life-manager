package io.github.doriangrelu.lifemanager.shopping.application.port.in.command;

import java.util.UUID;
/** Un membre ({@code sub} Keycloak) appartient désormais à ce foyer. */
public record RegisterHouseholdMemberCommand(UUID householdId, UUID memberId) {
}
