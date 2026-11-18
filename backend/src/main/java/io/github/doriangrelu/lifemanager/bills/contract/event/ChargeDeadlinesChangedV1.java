package io.github.doriangrelu.lifemanager.bills.contract.event;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Événement public : prochaine échéance ouverte d'une charge, pour les rappels.
 *
 * @param nextDueOn            {@code null} s'il n'y a plus d'échéance ouverte (charge résiliée)
 * @param awaitingConfirmation vrai si cette échéance attend la confirmation de son montant
 */
@Serdeable
public record ChargeDeadlinesChangedV1(UUID chargeId, UUID householdId, String chargeName, @Nullable LocalDate nextDueOn,
                                       boolean awaitingConfirmation, Instant occurredAt) {
}
