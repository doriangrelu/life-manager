package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.budget.application.port.in.result.BeneficiaryTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.MonthCategoryAmount;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.MonthResult;
import io.github.doriangrelu.lifemanager.budget.application.port.out.BudgetReportReadModel;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.IncomeTotalsJdbcRepository;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.MonthlyTotalsJdbcRepository;
import jakarta.inject.Singleton;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/** Implémente les lectures du bilan avec des requêtes SQL d'agrégation. */
@Singleton
class BudgetReportReadModelAdapter implements BudgetReportReadModel {

    private final IncomeTotalsJdbcRepository incomes;
    private final MonthlyTotalsJdbcRepository expenses;

    BudgetReportReadModelAdapter(IncomeTotalsJdbcRepository incomes, MonthlyTotalsJdbcRepository expenses) {
        this.incomes = incomes;
        this.expenses = expenses;
    }

    @Override
    public List<CategoryTotal> incomeByCategory(HouseholdId householdId, LocalDate firstDay, LocalDate lastDay) {
        return incomes.totalsByCategory(householdId.value(), firstDay, lastDay).stream()
                .map(row -> new CategoryTotal(row.categoryId(), row.name(), row.totalCents()))
                .toList();
    }

    @Override
    public List<BeneficiaryTotal> incomeByReceiver(HouseholdId householdId, LocalDate firstDay, LocalDate lastDay) {
        return incomes.totalsByReceiver(householdId.value(), firstDay, lastDay).stream()
                .map(row -> new BeneficiaryTotal(row.memberId(), row.totalCents()))
                .toList();
    }

    @Override
    public List<CategoryTotal> expenseByCategory(HouseholdId householdId, LocalDate firstDay, LocalDate lastDay) {
        return expenses.totalsByCategory(householdId.value(), firstDay, lastDay).stream()
                .map(row -> new CategoryTotal(row.categoryId(), row.name(), row.totalCents()))
                .toList();
    }

    @Override
    public long recurringExpenseCents(HouseholdId householdId, LocalDate firstDay, LocalDate lastDay) {
        return expenses.recurringTotal(householdId.value(), firstDay, lastDay);
    }

    @Override
    public List<MonthCategoryAmount> expensesByMonthAndCategory(HouseholdId householdId, LocalDate firstDay,
                                                                LocalDate lastDay) {
        return expenses.totalsByMonthAndCategory(householdId.value(), firstDay, lastDay).stream()
                .map(row -> new MonthCategoryAmount(YearMonth.parse(row.month()), row.categoryId(), row.totalCents()))
                .toList();
    }

    @Override
    public List<MonthResult> months(HouseholdId householdId, LocalDate firstDay, LocalDate lastDay) {
        return incomes.monthlyTotals(householdId.value(), firstDay, lastDay).stream()
                .map(row -> new MonthResult(YearMonth.parse(row.month()), row.incomeCents(), row.expenseCents()))
                .toList();
    }
}
