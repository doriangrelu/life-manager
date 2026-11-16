package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import java.time.YearMonth;
import java.util.List;
/**
 * Vue d'un mois : total, répartitions et détail. Tous les montants sont en centimes.
 */
public record MonthlyBudget(YearMonth month, long totalCents, List<CategoryTotal> byCategory,
                            List<PayerTotal> byPayer, List<BeneficiaryTotal> byBeneficiary,
                            List<ExpenseView> expenses) {
}
