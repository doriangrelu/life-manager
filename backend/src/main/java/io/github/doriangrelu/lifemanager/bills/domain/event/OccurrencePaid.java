package io.github.doriangrelu.lifemanager.bills.domain.event;

import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Amount;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeDetails;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceId;

import java.time.Instant;
import java.time.LocalDate;

/** Une échéance a été payée : le budget en fera une dépense. */
public record OccurrencePaid(OccurrenceId occurrenceId, HouseholdId householdId, ChargeDetails charge, Amount paid,
                             LocalDate paidOn, Instant occurredAt) implements DomainEvent {
}
