package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.budget.application.port.in.result.BeneficiaryTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.CategoryTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.ExpenseView;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.PayerTotal;
import io.github.doriangrelu.lifemanager.budget.application.port.out.MonthlyBudgetReadModel;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.mapper.ExpenseMapper;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.ExpenseJdbcRepository;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository.MonthlyTotalsJdbcRepository;
import jakarta.inject.Singleton;

import java.time.YearMonth;
import java.util.List;

/** Implémente les lectures de la vue mensuelle avec des requêtes SQL d'agrégation. */
@Singleton
class MonthlyBudgetReadModelAdapter implements MonthlyBudgetReadModel {

    private final MonthlyTotalsJdbcRepository totals;
    private final ExpenseJdbcRepository expenses;

    MonthlyBudgetReadModelAdapter(MonthlyTotalsJdbcRepository totals, ExpenseJdbcRepository expenses) {
        this.totals = totals;
        this.expenses = expenses;
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
