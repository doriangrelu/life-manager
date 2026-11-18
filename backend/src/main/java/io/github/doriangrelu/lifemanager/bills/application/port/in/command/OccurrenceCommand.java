package io.github.doriangrelu.lifemanager.bills.application.port.in.command;

import java.util.UUID;

/** Geste sur une échéance (sauter, lister ses factures). */
public record OccurrenceCommand(String requesterId, UUID householdId, UUID chargeId, UUID occurrenceId) {
}
