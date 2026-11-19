package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.budget.application.port.in.result.BeneficiaryTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.ExpenseView;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.IncomeView;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.PayerTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.out.MonthlyBudgetReadModel;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.mapper.ExpenseMapper;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.mapper.IncomeMapper;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.ExpenseJdbcRepository;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.IncomeJdbcRepository;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.MonthlyTotalsJdbcRepository;
import jakarta.inject.Singleton;

import java.time.YearMonth;
import java.util.List;

/** Implémente les lectures de la vue mensuelle avec des requêtes SQL d'agrégation. */
@Singleton
class MonthlyBudgetReadModelAdapter implements MonthlyBudgetReadModel {

    private final MonthlyTotalsJdbcRepository totals;
    private final ExpenseJdbcRepository expenses;
    private final IncomeJdbcRepository incomes;

    MonthlyBudgetReadModelAdapter(MonthlyTotalsJdbcRepository totals, ExpenseJdbcRepository expenses,
                                  IncomeJdbcRepository incomes) {
        this.totals = totals;
        this.expenses = expenses;
        this.incomes = incomes;
    }

    @Override
    public List<IncomeView> incomes(HouseholdId householdId, YearMonth month) {
        return incomes.findForPeriod(householdId.value(), month.atDay(1), month.atEndOfMonth()).stream()
                .map(IncomeMapper::toDomain)
                .map(IncomeView::from)
                .toList();
    }

    @Override
    public List<CategoryTotal> totalsByCategory(HouseholdId householdId, YearMonth month) {
        return totals.totalsByCategory(householdId.value(), month.atDay(1), month.atEndOfMonth()).stream()
                .map(row -> new CategoryTotal(row.categoryId(), row.name(), row.totalCents()))
                .toList();
    }

    @Override
    public List<PayerTotal> totalsByPayer(HouseholdId householdId, YearMonth month) {
        return totals.totalsByPayer(householdId.value(), month.atDay(1), month.atEndOfMonth()).stream()
                .map(row -> new PayerTotal(row.memberId(), row.totalCents()))
                .toList();
    }

    @Override
    public List<BeneficiaryTotal> totalsByBeneficiary(HouseholdId householdId, YearMonth month) {
        return totals.totalsByBeneficiary(householdId.value(), month.atDay(1), month.atEndOfMonth()).stream()
                .map(row -> new BeneficiaryTotal(row.memberId(), row.totalCents()))
                .toList();
    }

    @Override
    public List<ExpenseView> expenses(HouseholdId householdId, YearMonth month) {
        return expenses.findForPeriod(householdId.value(), month.atDay(1), month.atEndOfMonth()).stream()
                .map(ExpenseMapper::toDomain)
                .map(ExpenseView::from)
                .toList();
    }
}
