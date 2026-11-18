package io.github.doriangrelu.lifemanager.bills.application.port.in.command;

import java.util.UUID;

/** Geste sur une charge (résilier, lister ses échéances). */
public record ChargeCommand(String requesterId, UUID householdId, UUID chargeId) {
}
