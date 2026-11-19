package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Ligne de la table {@code budget.income}. Montant en centimes d'euro.
 *
 * @param receivedBy membre qui reçoit, ou {@code null} pour le foyer entier
 */
@MappedEntity("income")
public record IncomeEntity(@Id UUID id, UUID householdId, UUID categoryId, long amountCents, LocalDate receivedOn,
                           @Nullable UUID receivedBy, @Nullable String label, UUID recordedBy, Instant recordedAt) {
}
