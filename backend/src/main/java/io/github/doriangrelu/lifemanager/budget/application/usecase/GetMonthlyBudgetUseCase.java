package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.GetMonthlyBudget;
import io.github.doriangrelu.lifemanager.budget.application.port.in.query.GetMonthlyBudgetQuery;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.MonthlyBudget;
import io.github.doriangrelu.lifemanager.budget.application.port.out.BudgetReportReadModel;
import io.github.doriangrelu.lifemanager.budget.application.port.out.MonthlyBudgetReadModel;
import io.github.doriangrelu.lifemanager.budget.application.service.BudgetAccess;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryPlan;
import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryBudgetRepository;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.CategoryBudget;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.YearMonth;
import java.util.List;

/**
 * Vue d'un mois pour un membre du foyer : dépenses (total, répartitions, détail),
 * rentrées (total, par catégorie, détail) et résultat.
 */
@Singleton
class GetMonthlyBudgetUseCase implements GetMonthlyBudget {

    private final BudgetAccess access;
    private final MonthlyBudgetReadModel readModel;
    private final BudgetReportReadModel report;
    private final CategoryBudgetRepository budgets;

    GetMonthlyBudgetUseCase(BudgetAccess access, MonthlyBudgetReadModel readModel, BudgetReportReadModel report,
                            CategoryBudgetRepository budgets) {
        this.access = access;
        this.readModel = readModel;
        this.report = report;
        this.budgets = budgets;
    }

    @Override
    @Transactional(transactionManager = "budget", readOnly = true)
    public MonthlyBudget get(GetMonthlyBudgetQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        var month = query.month();
        var byCategory = readModel.totalsByCategory(household, month);
        var incomeByCategory = report.incomeByCategory(household, month.atDay(1), month.atEndOfMonth());
        return new MonthlyBudget(month, CategoryTotal.sum(byCategory), byCategory,
                readModel.totalsByPayer(household, month),
                readModel.totalsByBeneficiary(household, month),
                readModel.expenses(household, month),
                CategoryTotal.sum(incomeByCategory), incomeByCategory,
                readModel.incomes(household, month),
                plans(household, month),
                report.recurringExpenseCents(household, month.atDay(1), month.atEndOfMonth()));
    }

    /** Le prévu de chaque catégorie pour ce mois, d'après les règles datées du foyer. */
    private List<CategoryPlan> plans(HouseholdId household, YearMonth month) {
        return CategoryBudget.applicableIn(budgets.findAllUntil(household, month), month).stream()
                .map(CategoryPlan::from)
                .toList();
    }
}
