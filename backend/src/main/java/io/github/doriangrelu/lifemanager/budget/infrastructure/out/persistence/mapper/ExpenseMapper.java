package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Expense;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Beneficiary;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseDetails;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseLabel;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Money;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ProjectId;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.ExpenseEntity;

/** Conversion entre l'agrégat {@link Expense} et sa ligne en base. */
public final class ExpenseMapper {

    private ExpenseMapper() {
    }

    public static ExpenseEntity toEntity(Expense expense) {
        var details = expense.details();
        return new ExpenseEntity(
                expense.id().value(),
                expense.householdId().value(),
                details.categoryId().value(),
                details.amount().cents(),
                details.spentOn(),
                details.paidBy().value(),
                details.beneficiary().member().map(MemberId::value).orElse(null),
                details.optionalLabel().map(ExpenseLabel::value).orElse(null),
                expense.recordedBy().value(),
                expense.recordedAt(),
                details.projectId() == null ? null : details.projectId().value());
    }

    public static Expense toDomain(ExpenseEntity entity) {
        return Expense.restore(new ExpenseId(entity.id()), new HouseholdId(entity.householdId()), details(entity),
                new MemberId(entity.recordedBy()), entity.recordedAt());
    }

    private static ExpenseDetails details(ExpenseEntity entity) {
        return new ExpenseDetails(
                Money.ofCents(entity.amountCents()),
                new CategoryId(entity.categoryId()),
                entity.spentOn(),
                new MemberId(entity.paidBy()),
                Beneficiary.of(entity.beneficiary()),
                ExpenseLabel.ofNullable(entity.label()),
                ProjectId.ofNullable(entity.projectId()));
    }
}
