package io.github.doriangrelu.lifemanager.vehicle.domain.event;

import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.ExpenseTarget;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Le coût d'une opération est envoyé au budget.
 *
 * @param label libellé de la dépense (« Révision · Clio »)
 */
public record MaintenanceExpenseSent(MaintenanceEntryId entryId, HouseholdId householdId, String label,
                                     long amountCents, LocalDate performedOn, ExpenseTarget target,
                                     Instant occurredAt) implements DomainEvent {
}
