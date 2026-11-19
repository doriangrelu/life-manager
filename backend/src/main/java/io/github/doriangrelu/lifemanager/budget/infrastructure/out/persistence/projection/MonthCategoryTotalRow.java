package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.projection;

import io.micronaut.core.annotation.Introspected;

import java.util.UUID;

/**
 * Dépenses d'une catégorie sur un mois, en centimes.
 *
 * @param month mois au format AAAA-MM
 */
@Introspected
public record MonthCategoryTotalRow(String month, UUID categoryId, long totalCents) {
}
