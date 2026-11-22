package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/**
 * Où va le coût d'une opération dans le budget.
 *
 * @param categoryId catégorie de dépense du budget (vérifiée par le budget, qui prend « Autre » si elle est inconnue)
 * @param payer      membre du foyer qui a payé
 */
public record ExpenseTarget(UUID categoryId, MemberId payer) {

    public ExpenseTarget {
        Require.notNull(categoryId, "La catégorie");
        Require.notNull(payer, "Le payeur");
    }
}
