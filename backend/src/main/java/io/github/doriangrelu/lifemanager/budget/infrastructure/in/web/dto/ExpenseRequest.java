package io.github.doriangrelu.lifemanager.budget.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.ExpenseInput;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Corps de saisie ou de correction d'une dépense.
 *
 * @param amountCents montant en centimes d'euro (12,50 € = 1250)
 * @param beneficiary membre pour qui, ou {@code null} pour une dépense commune
 */
@Serdeable
public record ExpenseRequest(@Positive @Max(100_000_000) long amountCents,
                             @NotNull UUID categoryId,
                             @NotNull LocalDate spentOn,
                             @NotNull UUID paidBy,
                             @Nullable UUID beneficiary,
                             @Nullable @Size(max = 140) String label) {

    public ExpenseInput toInput() {
        return new ExpenseInput(amountCents, categoryId, spentOn, paidBy, beneficiary, label);
    }
}
