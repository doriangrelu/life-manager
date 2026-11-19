package io.github.doriangrelu.lifemanager.household.domain.event;

import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;

import java.time.Instant;

/** Le foyer a été supprimé par son propriétaire : toutes ses données doivent disparaître. */
public record HouseholdDeleted(HouseholdId householdId, Instant occurredAt) implements DomainEvent {
}
