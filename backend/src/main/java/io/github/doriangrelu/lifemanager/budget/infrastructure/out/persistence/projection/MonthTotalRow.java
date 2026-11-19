package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.projection;

import io.micronaut.core.annotation.Introspected;

/**
 * Rentrées et dépenses d'un mois, en centimes.
 *
 * @param month mois au format AAAA-MM
 */
@Introspected
public record MonthTotalRow(String month, long incomeCents, long expenseCents) {
}
