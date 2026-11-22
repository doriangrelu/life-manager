package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Expense;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseLabel;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ProjectId;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
/**
 * Une dépense telle que vue hors du domaine. Montant en centimes d'euro.
 *
 * @param beneficiary membre pour qui, ou {@code null} pour une dépense commune
 * @param label       libellé, ou {@code null}
 * @param projectId   projet rattaché, ou {@code null}
 * @param offBudget   vrai si son projet est réglé « hors budget » : elle ne compte pas dans les totaux
 */
public record ExpenseView(UUID id, long amountCents, UUID categoryId, LocalDate spentOn, UUID paidBy,
                          UUID beneficiary, String label, UUID projectId, boolean offBudget) {

    public static ExpenseView from(Expense expense) {
        return from(expense, Set.of());
    }

    /** Vue de la dépense, sachant quels projets du foyer sont hors budget. */
    public static ExpenseView from(Expense expense, Set<ProjectId> offBudgetProjects) {
        var details = expense.details();
        var project = details.projectId();
        return new ExpenseView(expense.id().value(), details.amount().cents(), details.categoryId().value(),
                details.spentOn(), details.paidBy().value(),
                details.beneficiary().member().map(MemberId::value).orElse(null),
                details.optionalLabel().map(ExpenseLabel::value).orElse(null),
                project == null ? null : project.value(), project != null && offBudgetProjects.contains(project));
    }
}
