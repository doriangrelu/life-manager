package io.github.doriangrelu.lifemanager.vehicle.domain.event;

import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;

import java.time.Instant;

/** L'opération envoyée au budget est supprimée : sa dépense doit partir aussi. */
public record MaintenanceExpenseWithdrawn(MaintenanceEntryId entryId, HouseholdId householdId, Instant occurredAt)
        implements DomainEvent {
}
