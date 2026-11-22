package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.util.UUID;

/** Ligne de la table {@code budget.charge_expense} : l'échéance de charge et la dépense qu'elle a produite. */
@MappedEntity("charge_expense")
public record ChargeExpenseEntity(@Id UUID occurrenceId, UUID expenseId, @Nullable UUID householdId) {
}
