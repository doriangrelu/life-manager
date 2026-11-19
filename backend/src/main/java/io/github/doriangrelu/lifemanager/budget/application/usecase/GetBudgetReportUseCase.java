package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.GetBudgetReport;
import io.github.doriangrelu.lifemanager.budget.application.port.in.query.GetBudgetReportQuery;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.BudgetReport;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.MonthCategoryAmount;
import io.github.doriangrelu.lifemanager.budget.application.port.out.BudgetReportReadModel;
import io.github.doriangrelu.lifemanager.budget.application.port.out.CategoryBudgetRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.BudgetAccess;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.CategoryBudget;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;

/** Bilan réalisé d'une période pour un membre du foyer (le prévu est ajouté par la PWA). */
@Singleton
class GetBudgetReportUseCase implements GetBudgetReport {

    private final BudgetAccess access;
    private final BudgetReportReadModel report;
    private final CategoryBudgetRepository budgets;

    GetBudgetReportUseCase(BudgetAccess access, BudgetReportReadModel report, CategoryBudgetRepository budgets) {
        this.access = access;
        this.report = report;
        this.budgets = budgets;
    }

    @Override
    @Transactional(transactionManager = "budget", readOnly = true)
    public BudgetReport get(GetBudgetReportQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        query.ensureValidPeriod();
        var firstDay = query.from().atDay(1);
        var lastDay = query.to().atEndOfMonth();
        return new BudgetReport(query.from(), query.to(),
                report.incomeByCategory(household, firstDay, lastDay),
                report.incomeByReceiver(household, firstDay, lastDay),
                report.expenseByCategory(household, firstDay, lastDay),
                report.months(household, firstDay, lastDay),
                report.recurringExpenseCents(household, firstDay, lastDay),
                monthPlans(household, query.from(), query.to()),
                report.expensesByMonthAndCategory(household, firstDay, lastDay));
    }

    /** Le prévu de chaque catégorie pour chaque mois de la période, d'après les règles datées du foyer. */
    private List<MonthCategoryAmount> monthPlans(HouseholdId household, YearMonth from, YearMonth to) {
        var rules = budgets.findAllUntil(household, to);
        return Stream.iterate(from, month -> !month.isAfter(to), month -> month.plusMonths(1))
                .flatMap(month -> CategoryBudget.applicableIn(rules, month).stream()
                        .map(rule -> new MonthCategoryAmount(month, rule.categoryId().value(),
                                rule.planned().orElseThrow().cents())))
                .toList();
    }
}
