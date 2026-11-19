package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.LocalDate;
import java.util.UUID;

/** Ligne de la table {@code bills.recurring_charge}. Montant en centimes d'euro. */
@MappedEntity("recurring_charge")
public record RecurringChargeEntity(@Id UUID id, UUID householdId, String name, long amountCents, String amountKind,
                                    String frequency, LocalDate firstDueOn, UUID budgetCategoryId, UUID payerId,
                                    @Nullable UUID beneficiaryId, boolean active, String direction) {
}
