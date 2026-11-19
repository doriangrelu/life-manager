package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.GetBudgetReport;
import io.github.doriangrelu.lifemanager.budget.application.port.in.query.GetBudgetReportQuery;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.BudgetReport;
import io.github.doriangrelu.lifemanager.budget.application.port.out.BudgetReportReadModel;
import io.github.doriangrelu.lifemanager.budget.application.service.BudgetAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Bilan réalisé d'une période pour un membre du foyer (le prévu est ajouté par la PWA). */
@Singleton
class GetBudgetReportUseCase implements GetBudgetReport {

    private final BudgetAccess access;
    private final BudgetReportReadModel report;

    GetBudgetReportUseCase(BudgetAccess access, BudgetReportReadModel report) {
        this.access = access;
        this.report = report;
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
                report.recurringExpenseCents(household, firstDay, lastDay));
    }
}
