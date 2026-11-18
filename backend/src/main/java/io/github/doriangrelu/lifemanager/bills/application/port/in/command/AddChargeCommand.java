package io.github.doriangrelu.lifemanager.bills.application.port.in.command;

import java.util.UUID;

/** Nouvelle charge dans un foyer. */
public record AddChargeCommand(String requesterId, UUID householdId, ChargeInput input) {
}
