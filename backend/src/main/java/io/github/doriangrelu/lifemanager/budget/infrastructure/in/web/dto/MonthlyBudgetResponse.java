package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.budget.application.port.in.result.MonthlyBudget;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.UUID;

/**
 * Réponse de la vue mensuelle. Tous les montants sont en centimes d'euro.
 *
 * @param totalCents  total des dépenses
 * @param incomeCents total des rentrées (0 sans suivi des rentrées)
 * @param resultCents rentrées moins dépenses, négatif possible
 * @param plans       budget prévu des catégories qui en ont un ce mois-là
 * @param recurringExpenseCents part des dépenses née d'une dépense récurrente (pour le reste à vivre)
 */
@Serdeable
public record MonthlyBudgetResponse(String month, long totalCents, List<CategoryTotal> byCategory,
                                    List<MemberTotal> byPayer, List<MemberTotal> byBeneficiary,
                                    List<ExpenseResponse> expenses, long incomeCents, long resultCents,
                                    List<CategoryTotal> incomeByCategory, List<IncomeResponse> incomes,
                                    List<Plan> plans, long recurringExpenseCents) {

    /** Budget prévu d'une catégorie ; {@code scope} : FROM_MONTH (tous les mois) ou MONTH_ONLY (ce mois seulement). */
    @Serdeable
    public record Plan(UUID categoryId, long plannedCents, String scope) {
    }

    /** Total d'une catégorie. */
    @Serdeable
    public record CategoryTotal(UUID categoryId, String name, long totalCents) {
    }

    /** Total d'un membre ; {@code memberId} vaut {@code null} pour la part commune (foyer). */
    @Serdeable
    public record MemberTotal(@Nullable UUID memberId, long totalCents) {
    }

    public static MonthlyBudgetResponse from(MonthlyBudget budget) {
        return new MonthlyBudgetResponse(
                budget.month().toString(),
                budget.totalCents(),
                categories(budget.byCategory()),
                budget.byPayer().stream().map(t -> new MemberTotal(t.memberId(), t.totalCents())).toList(),
                budget.byBeneficiary().stream().map(t -> new MemberTotal(t.memberId(), t.totalCents())).toList(),
                budget.expenses().stream().map(ExpenseResponse::from).toList(),
                budget.incomeCents(),
                budget.resultCents(),
                categories(budget.incomeByCategory()),
                budget.incomes().stream().map(IncomeResponse::from).toList(),
                budget.plans().stream().map(p -> new Plan(p.categoryId(), p.plannedCents(), p.scope())).toList(),
                budget.recurringExpenseCents());
    }

    static List<CategoryTotal> categories(
            List<io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryTotal> totals) {
        return totals.stream().map(t -> new CategoryTotal(t.categoryId(), t.name(), t.totalCents())).toList();
    }
}
