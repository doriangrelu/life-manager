package io.github.doriangrelu.lifemanager.bills.application.port.in.query;

import java.util.UUID;

/** Charges d'un foyer. */
public record ListChargesQuery(String requesterId, UUID householdId) {
}
