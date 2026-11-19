package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Ligne de la table {@code budget.category_budget}. {@code month} est le premier jour du mois ;
 * {@code plannedCents} absent = la règle retire le prévu.
 */
@MappedEntity("category_budget")
public record CategoryBudgetEntity(@Id UUID id, UUID householdId, UUID categoryId, LocalDate month, String scope,
                                   @Nullable Long plannedCents) {
}
