package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.budget.application.port.in.result.ExpenseView;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Une dépense. Montant en centimes d'euro.
 *
 * @param beneficiary membre pour qui, ou {@code null} pour une dépense commune
 */
@Serdeable
public record ExpenseResponse(UUID id, long amountCents, UUID categoryId, LocalDate spentOn, UUID paidBy,
                              @Nullable UUID beneficiary, @Nullable String label) {

    public static ExpenseResponse from(ExpenseView expense) {
        return new ExpenseResponse(expense.id(), expense.amountCents(), expense.categoryId(), expense.spentOn(),
                expense.paidBy(), expense.beneficiary(), expense.label());
    }
}
