package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.GetMonthlyBudget;
import io.github.doriangrelu.lifemanager.budget.application.port.in.query.GetMonthlyBudgetQuery;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.MonthlyBudget;
import io.github.doriangrelu.lifemanager.budget.application.port.out.MonthlyBudgetReadModel;
import io.github.doriangrelu.lifemanager.budget.application.service.BudgetAccess;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.Money;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Vue d'un mois pour un membre du foyer : total, répartitions par catégorie et par personne, détail. */
@Singleton
class GetMonthlyBudgetUseCase implements GetMonthlyBudget {

    private final BudgetAccess access;
    private final MonthlyBudgetReadModel readModel;

    GetMonthlyBudgetUseCase(BudgetAccess access, MonthlyBudgetReadModel readModel) {
        this.access = access;
        this.readModel = readModel;
    }

    @Override
    @Transactional(transactionManager = "budget", readOnly = true)
    public MonthlyBudget get(GetMonthlyBudgetQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        var month = query.month();
        var byCategory = readModel.totalsByCategory(household, month);
        return new MonthlyBudget(month, total(byCategory), byCategory,
                readModel.totalsByPayer(household, month),
                readModel.totalsByBeneficiary(household, month),
                readModel.expenses(household, month));
    }

    // Somme exacte en centimes (Money refuse tout dépassement de capacité).
    private static long total(List<CategoryTotal> byCategory) {
        return byCategory.stream()
                .map(category -> Money.ofCents(category.totalCents()))
                .reduce(Money.ZERO, Money::plus)
                .cents();
    }
}
