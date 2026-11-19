package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Income;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Beneficiary;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseLabel;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeDetails;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Money;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.IncomeEntity;

/** Conversion entre l'agrégat {@link Income} et sa ligne en base. */
public final class IncomeMapper {

    private IncomeMapper() {
    }

    public static IncomeEntity toEntity(Income income) {
        var details = income.details();
        return new IncomeEntity(
                income.id().value(),
                income.householdId().value(),
                details.categoryId().value(),
                details.amount().cents(),
                details.receivedOn(),
                details.receivedBy().member().map(MemberId::value).orElse(null),
                details.optionalLabel().map(ExpenseLabel::value).orElse(null),
                income.recordedBy().value(),
                income.recordedAt());
    }

    public static Income toDomain(IncomeEntity entity) {
        var details = new IncomeDetails(
                Money.ofCents(entity.amountCents()),
                new CategoryId(entity.categoryId()),
                entity.receivedOn(),
                Beneficiary.of(entity.receivedBy()),
                ExpenseLabel.ofNullable(entity.label()));
        return Income.restore(new IncomeId(entity.id()), new HouseholdId(entity.householdId()), details,
                new MemberId(entity.recordedBy()), entity.recordedAt());
    }
}
