package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.ArchiveCategory;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CategoryActionCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryView;
import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryBudgetRepository;
import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.CategoryAccess;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Category;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.CategoryBudget;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.BudgetScope;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.YearMonth;

/**
 * Archive une catégorie : elle n'est plus proposée pour les nouvelles dépenses, et son budget prévu
 * s'arrête au mois de l'archivage (les mois passés le gardent).
 */
@Singleton
class ArchiveCategoryUseCase implements ArchiveCategory {

    private final CategoryAccess categoryAccess;
    private final CategoryRepository categories;
    private final CategoryBudgetRepository budgets;
    private final Clock clock;

    ArchiveCategoryUseCase(CategoryAccess categoryAccess, CategoryRepository categories,
                           CategoryBudgetRepository budgets, Clock clock) {
        this.categoryAccess = categoryAccess;
        this.categories = categories;
        this.budgets = budgets;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public CategoryView archive(CategoryActionCommand command) {
        var category = categoryAccess.loadForMember(command.householdId(), command.kind(), command.categoryId(),
                command.requesterId());
        category.archive();
        categories.save(category);
        if (category.kind() == CategoryKind.EXPENSE) {
            stopPlanFromThisMonth(category);
        }
        return CategoryView.from(category);
    }

    // « Plus de prévu » à partir de ce mois : l'ajustement ponctuel de ce mois disparaît aussi.
    private void stopPlanFromThisMonth(Category category) {
        var month = YearMonth.now(clock);
        budgets.delete(category.id(), month, BudgetScope.MONTH_ONLY);
        budgets.save(CategoryBudget.set(category.householdId(), category.id(),
                new CategoryBudget.Period(month, BudgetScope.FROM_MONTH), null));
    }
}
