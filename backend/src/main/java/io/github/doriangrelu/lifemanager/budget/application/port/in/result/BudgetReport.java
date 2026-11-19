package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import java.time.YearMonth;
import java.util.List;

/**
 * Bilan réalisé d'une période de mois entiers. Montants en centimes.
 *
 * @param from             premier mois inclus
 * @param to               dernier mois inclus
 * @param incomeByReceiver rentrées par personne ({@code null} = foyer entier)
 * @param months           rentrées et dépenses de chaque mois qui en a
 * @param recurringExpenseCents part des dépenses née d'une dépense récurrente
 */
public record BudgetReport(YearMonth from, YearMonth to, List<CategoryTotal> incomeByCategory,
                           List<BeneficiaryTotal> incomeByReceiver, List<CategoryTotal> expenseByCategory,
                           List<MonthResult> months, long recurringExpenseCents) {

    public long incomeCents() {
        return CategoryTotal.sum(incomeByCategory);
    }

    public long expenseCents() {
        return CategoryTotal.sum(expenseByCategory);
    }

    /** Positif : épargne de la période ; négatif : période dans le rouge. */
    public long resultCents() {
        return Math.subtractExact(incomeCents(), expenseCents());
    }
}
