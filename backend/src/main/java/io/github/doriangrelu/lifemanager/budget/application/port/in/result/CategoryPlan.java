package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.CategoryBudget;

import java.util.UUID;

/**
 * Budget prévu d'une catégorie pour le mois consulté.
 *
 * @param plannedCents montant prévu en centimes
 * @param scope        portée de la règle appliquée : « FROM_MONTH » ou « MONTH_ONLY »
 */
public record CategoryPlan(UUID categoryId, long plannedCents, String scope) {

    /** La règle doit fixer un montant (voir {@link CategoryBudget#applicableIn}). */
    public static CategoryPlan from(CategoryBudget rule) {
        return new CategoryPlan(rule.categoryId().value(), rule.planned().orElseThrow().cents(),
                rule.scope().name());
    }
}
