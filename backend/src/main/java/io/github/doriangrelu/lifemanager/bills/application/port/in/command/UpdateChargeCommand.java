package io.github.doriangrelu.lifemanager.bills.application.port.in.command;

import java.util.UUID;

/** Modification d'une charge. */
public record UpdateChargeCommand(String requesterId, UUID householdId, UUID chargeId, ChargeInput input) {
}
