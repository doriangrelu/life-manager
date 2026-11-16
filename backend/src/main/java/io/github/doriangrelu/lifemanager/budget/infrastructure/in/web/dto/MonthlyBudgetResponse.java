package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.budget.application.port.in.result.MonthlyBudget;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.UUID;

/** Réponse de la vue mensuelle. Tous les montants sont en centimes d'euro. */
@Serdeable
public record MonthlyBudgetResponse(String month, long totalCents, List<CategoryTotal> byCategory,
                                    List<MemberTotal> byPayer, List<MemberTotal> byBeneficiary,
                                    List<ExpenseResponse> expenses) {

    /** Total d'une catégorie. */
    @Serdeable
    public record CategoryTotal(UUID categoryId, String name, long totalCents) {
    }

    /** Total d'un membre ; {@code memberId} vaut {@code null} pour les dépenses communes (bénéficiaires). */
    @Serdeable
    public record MemberTotal(@Nullable UUID memberId, long totalCents) {
    }

    public static MonthlyBudgetResponse from(MonthlyBudget budget) {
        return new MonthlyBudgetResponse(
                budget.month().toString(),
                budget.totalCents(),
                budget.byCategory().stream().map(t -> new CategoryTotal(t.categoryId(), t.name(), t.totalCents()))
                        .toList(),
                budget.byPayer().stream().map(t -> new MemberTotal(t.memberId(), t.totalCents())).toList(),
                budget.byBeneficiary().stream().map(t -> new MemberTotal(t.memberId(), t.totalCents())).toList(),
                budget.expenses().stream().map(ExpenseResponse::from).toList());
    }
}
