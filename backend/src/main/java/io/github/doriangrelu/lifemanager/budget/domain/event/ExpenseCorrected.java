package io.github.doriangrelu.lifemanager.budget.domain.event;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;

import java.time.Instant;

/** Une dépense a été corrigée. */
public record ExpenseCorrected(ExpenseId expenseId, HouseholdId householdId, Instant occurredAt) implements DomainEvent {
}
