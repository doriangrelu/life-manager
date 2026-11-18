package io.github.doriangrelu.lifemanager.bills.domain.event;

import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeName;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Prochaine échéance ouverte d'une charge (pour les rappels).
 *
 * @param nextDueOn             date de cette échéance, {@code null} s'il n'y en a plus (charge résiliée)
 * @param awaitingConfirmation  vrai si elle attend la confirmation de son montant
 */
public record ChargeDeadlineChanged(ChargeId chargeId, HouseholdId householdId, ChargeName name, LocalDate nextDueOn,
                                    boolean awaitingConfirmation, Instant occurredAt) implements DomainEvent {
}
