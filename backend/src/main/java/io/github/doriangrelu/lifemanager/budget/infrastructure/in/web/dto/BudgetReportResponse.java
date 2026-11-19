package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.budget.application.port.in.result.BudgetReport;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.MonthCategoryAmount;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.MonthlyBudgetResponse.CategoryTotal;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.MonthlyBudgetResponse.MemberTotal;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.UUID;

/**
 * Bilan réalisé d'une période. Montants en centimes d'euro.
 *
 * @param resultCents rentrées moins dépenses : positif = épargne, négatif = dans le rouge
 * @param recurringExpenseCents part des dépenses née d'une dépense récurrente (pour le reste à vivre)
 * @param monthPlans    budget prévu de chaque catégorie, mois par mois (pour le respect des budgets)
 * @param monthExpenses dépenses de chaque catégorie, mois par mois
 */
@Serdeable
public record BudgetReportResponse(String from, String to, long incomeCents, long expenseCents, long resultCents,
                                   List<CategoryTotal> incomeByCategory, List<MemberTotal> incomeByReceiver,
                                   List<CategoryTotal> expenseByCategory, List<Month> months,
                                   long recurringExpenseCents, List<MonthAmount> monthPlans,
                                   List<MonthAmount> monthExpenses) {

    /** Montant d'une catégorie pour un mois (« 2026-11 »), en centimes. */
    @Serdeable
    public record MonthAmount(String month, UUID categoryId, long cents) {

        static List<MonthAmount> from(List<MonthCategoryAmount> amounts) {
            return amounts.stream()
                    .map(a -> new MonthAmount(a.month().toString(), a.categoryId(), a.cents()))
                    .toList();
        }
    }

    /** Un mois de la période (seuls les mois qui ont des mouvements). */
    @Serdeable
    public record Month(String month, long incomeCents, long expenseCents, long resultCents) {
    }

    public static BudgetReportResponse from(BudgetReport report) {
        return new BudgetReportResponse(
                report.from().toString(),
                report.to().toString(),
                report.incomeCents(),
                report.expenseCents(),
                report.resultCents(),
                MonthlyBudgetResponse.categories(report.incomeByCategory()),
                report.incomeByReceiver().stream().map(t -> new MemberTotal(t.memberId(), t.totalCents())).toList(),
                MonthlyBudgetResponse.categories(report.expenseByCategory()),
                report.months().stream()
                        .map(m -> new Month(m.month().toString(), m.incomeCents(), m.expenseCents(), m.resultCents()))
                        .toList(),
                report.recurringExpenseCents(),
                MonthAmount.from(report.monthPlans()),
                MonthAmount.from(report.monthExpenses()));
    }
}
