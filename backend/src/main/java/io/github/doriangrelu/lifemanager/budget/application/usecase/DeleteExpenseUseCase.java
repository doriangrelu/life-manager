package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.DeleteExpense;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.DeleteExpenseCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.out.ExpenseRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.ExpenseAccess;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Supprime définitivement une dépense du foyer. */
@Singleton
class DeleteExpenseUseCase implements DeleteExpense {

    private final ExpenseAccess expenseAccess;
    private final ExpenseRepository expenses;
    private final DomainEventPublisher events;
    private final Clock clock;

    DeleteExpenseUseCase(ExpenseAccess expenseAccess, ExpenseRepository expenses, DomainEventPublisher events,
                         Clock clock) {
        this.expenseAccess = expenseAccess;
        this.expenses = expenses;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public void delete(DeleteExpenseCommand command) {
        var expense = expenseAccess.loadForMember(command.householdId(), command.expenseId(), command.requesterId());
        expense.markDeleted(clock);
        expenses.delete(expense);
        events.publishEventsOf(expense);
    }
}
