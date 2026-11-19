package io.github.doriangrelu.lifemanager.budget.application.port.in.result;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Income;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseLabel;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Une rentrée telle que vue hors du domaine. Montant en centimes d'euro.
 *
 * @param receivedBy membre qui la reçoit, ou {@code null} pour le foyer entier
 * @param label      libellé, ou {@code null}
 */
public record IncomeView(UUID id, long amountCents, UUID categoryId, LocalDate receivedOn, UUID receivedBy,
                         String label) {

    public static IncomeView from(Income income) {
        var details = income.details();
        return new IncomeView(income.id().value(), details.amount().cents(), details.categoryId().value(),
                details.receivedOn(), details.receivedBy().member().map(MemberId::value).orElse(null),
                details.optionalLabel().map(ExpenseLabel::value).orElse(null));
    }
}
