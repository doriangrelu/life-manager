package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryBudgetRepository;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.CategoryBudget;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.BudgetScope;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryBudgetId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.PlannedAmount;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.CategoryBudgetEntity;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.CategoryBudgetJdbcRepository;
import jakarta.inject.Singleton;

import java.time.YearMonth;
import java.util.List;

/** Implémente le stockage des budgets prévus avec PostgreSQL (table category_budget). */
@Singleton
class CategoryBudgetRepositoryAdapter implements CategoryBudgetRepository {

    private final CategoryBudgetJdbcRepository table;

    CategoryBudgetRepositoryAdapter(CategoryBudgetJdbcRepository table) {
        this.table = table;
    }

    @Override
    public void save(CategoryBudget rule) {
        table.upsert(new CategoryBudgetEntity(rule.id().value(), rule.householdId().value(),
                rule.categoryId().value(), rule.month().atDay(1), rule.scope().name(),
                rule.planned().map(PlannedAmount::cents).orElse(null)));
    }

    @Override
    public void delete(CategoryId categoryId, YearMonth month, BudgetScope scope) {
        table.deleteRule(categoryId.value(), month.atDay(1), scope.name());
    }

    @Override
    public List<CategoryBudget> findAllUntil(HouseholdId householdId, YearMonth month) {
        return table.findAllUntil(householdId.value(), month.atDay(1)).stream()
                .map(CategoryBudgetRepositoryAdapter::toDomain)
                .toList();
    }

    private static CategoryBudget toDomain(CategoryBudgetEntity entity) {
        var period = new CategoryBudget.Period(YearMonth.from(entity.month()), BudgetScope.of(entity.scope()));
        return CategoryBudget.restore(new CategoryBudgetId(entity.id()), new HouseholdId(entity.householdId()),
                new CategoryId(entity.categoryId()), period, PlannedAmount.ofNullable(entity.plannedCents()));
    }
}
