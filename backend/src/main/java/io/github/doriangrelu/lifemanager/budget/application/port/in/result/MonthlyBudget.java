package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import java.time.YearMonth;
import java.util.List;

/**
 * Vue d'un mois : dépenses (total, répartitions, détail) et rentrées (total, par catégorie, détail).
 * Tous les montants sont en centimes. Sans suivi des rentrées, la partie rentrées est simplement vide.
 *
 * @param totalCents  total des dépenses
 * @param incomeCents total des rentrées
 */
public record MonthlyBudget(YearMonth month, long totalCents, List<CategoryTotal> byCategory,
                            List<PayerTotal> byPayer, List<BeneficiaryTotal> byBeneficiary,
                            List<ExpenseView> expenses, long incomeCents, List<CategoryTotal> incomeByCategory,
                            List<IncomeView> incomes) {

    /** Rentrées moins dépenses : positif = argent mis de côté, négatif = mois dans le rouge. */
    public long resultCents() {
        return Math.subtractExact(incomeCents, totalCents);
    }
}
