package io.github.doriangrelu.lifemanager.budget.application.service;

import io.github.doriangrelu.lifemanager.budget.application.port.out.ExpenseRepository;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Expense;
import io.github.doriangrelu.lifemanager.budget.domain.exception.ExpenseNotFoundException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseId;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Retrouve une dépense d'un foyer pour l'un de ses membres (correction, suppression). */
@Singleton
public class ExpenseAccess {

    private final BudgetAccess access;
    private final ExpenseRepository expenses;

    ExpenseAccess(BudgetAccess access, ExpenseRepository expenses) {
        this.access = access;
        this.expenses = expenses;
    }

    /** @throws ExpenseNotFoundException si la dépense n'existe pas dans ce foyer */
    public Expense loadForMember(UUID householdId, UUID expenseId, String requesterId) {
        var household = access.householdOfMember(householdId, requesterId);
        return expenses.findById(new ExpenseId(expenseId))
                .filter(expense -> expense.belongsTo(household))
                .orElseThrow(ExpenseNotFoundException::new);
    }
}
