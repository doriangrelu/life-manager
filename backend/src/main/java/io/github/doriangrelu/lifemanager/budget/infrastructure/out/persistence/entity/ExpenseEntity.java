package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Ligne de la table {@code budget.expense}. Montant en centimes d'euro.
 *
 * @param beneficiary membre pour qui, ou {@code null} pour une dépense commune
 */
@MappedEntity("expense")
public record ExpenseEntity(@Id UUID id, UUID householdId, UUID categoryId, long amountCents, LocalDate spentOn,
                            UUID paidBy, @Nullable UUID beneficiary, @Nullable String label, UUID recordedBy,
                            Instant recordedAt) {
}
