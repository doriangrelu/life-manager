package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.LocalDate;
import java.util.UUID;

/** Ligne de la table {@code bills.charge_occurrence}. Montants en centimes d'euro. */
@MappedEntity("charge_occurrence")
public record ChargeOccurrenceEntity(@Id UUID id, UUID chargeId, UUID householdId, LocalDate dueOn, long expectedCents,
                                     String status, @Nullable Long paidCents, @Nullable LocalDate paidOn) {
}
