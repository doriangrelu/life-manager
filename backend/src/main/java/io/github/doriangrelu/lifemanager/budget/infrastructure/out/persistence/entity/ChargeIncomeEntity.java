package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.util.UUID;

/** Ligne de la table {@code budget.charge_income} : l'échéance de rentrée récurrente et la rentrée produite. */
@MappedEntity("charge_income")
public record ChargeIncomeEntity(@Id UUID occurrenceId, UUID incomeId, @Nullable UUID householdId) {
}
