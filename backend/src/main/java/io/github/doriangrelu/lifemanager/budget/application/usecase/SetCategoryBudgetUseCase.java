package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.SetCategoryBudget;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.SetCategoryBudgetCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryBudgetRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.CategoryAccess;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.CategoryBudget;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.BudgetScope;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.PlannedAmount;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Fixe ou retire le budget prévu d'une catégorie de dépenses du foyer. */
@Singleton
class SetCategoryBudgetUseCase implements SetCategoryBudget {

    private final CategoryAccess categoryAccess;
    private final CategoryBudgetRepository budgets;

    SetCategoryBudgetUseCase(CategoryAccess categoryAccess, CategoryBudgetRepository budgets) {
        this.categoryAccess = categoryAccess;
        this.budgets = budgets;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public void set(SetCategoryBudgetCommand command) {
        var category = categoryAccess.loadForMember(command.householdId(), CategoryKind.EXPENSE,
                command.categoryId(), command.requesterId());
        var planned = PlannedAmount.ofNullable(command.plannedCents());
        if (planned != null && category.archived()) {
            throw new InvalidValueException("Une catégorie archivée ne peut pas recevoir de budget prévu");
        }
        var period = new CategoryBudget.Period(command.month(), BudgetScope.of(command.scope()));
        if (period.scope() == BudgetScope.FROM_MONTH) {
            // « Tous les mois » vaut aussi pour le mois de départ : son ajustement ponctuel disparaît.
            budgets.delete(category.id(), period.month(), BudgetScope.MONTH_ONLY);
        }
        budgets.save(CategoryBudget.set(category.householdId(), category.id(), period, planned));
    }
}
