package io.github.doriangrelu.lifemanager.bills.application.port.in.command;

import java.util.UUID;

/** Un membre a rejoint (ou créé) un foyer. */
public record RegisterHouseholdMemberCommand(UUID householdId, UUID memberId) {
}
