package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.budget.application.port.in.result.BudgetReport;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.MonthlyBudgetResponse.CategoryTotal;
import io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto.MonthlyBudgetResponse.MemberTotal;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

/**
 * Bilan réalisé d'une période. Montants en centimes d'euro.
 *
 * @param resultCents rentrées moins dépenses : positif = épargne, négatif = dans le rouge
 * @param recurringExpenseCents part des dépenses née d'une dépense récurrente (pour le reste à vivre)
 */
@Serdeable
public record BudgetReportResponse(String from, String to, long incomeCents, long expenseCents, long resultCents,
                                   List<CategoryTotal> incomeByCategory, List<MemberTotal> incomeByReceiver,
                                   List<CategoryTotal> expenseByCategory, List<Month> months,
                                   long recurringExpenseCents) {

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
                report.recurringExpenseCents());
    }
}
