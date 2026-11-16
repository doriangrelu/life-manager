package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Expense;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseLabel;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;

import java.time.LocalDate;
import java.util.UUID;
/**
 * Une dépense telle que vue hors du domaine. Montant en centimes d'euro.
 *
 * @param beneficiary membre pour qui, ou {@code null} pour une dépense commune
 * @param label       libellé, ou {@code null}
 */
public record ExpenseView(UUID id, long amountCents, UUID categoryId, LocalDate spentOn, UUID paidBy,
                          UUID beneficiary, String label) {

    public static ExpenseView from(Expense expense) {
        var details = expense.details();
        return new ExpenseView(expense.id().value(), details.amount().cents(), details.categoryId().value(),
                details.spentOn(), details.paidBy().value(),
                details.beneficiary().member().map(MemberId::value).orElse(null),
                details.optionalLabel().map(ExpenseLabel::value).orElse(null));
    }
}
