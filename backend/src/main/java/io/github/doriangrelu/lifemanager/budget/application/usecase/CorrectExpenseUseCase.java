package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.CorrectExpense;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.CorrectExpenseCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.ExpenseView;
import io.github.doriangrelu.lifemanager.budget.application.port.out.ExpenseRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.ExpenseAccess;
import io.github.doriangrelu.lifemanager.budget.application.service.ExpenseRules;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Corrige une dépense : mêmes règles qu'à la saisie, et rien ne change si la correction est refusée. */
@Singleton
class CorrectExpenseUseCase implements CorrectExpense {

    private final ExpenseAccess expenseAccess;
    private final ExpenseRules rules;
    private final ExpenseRepository expenses;
    private final DomainEventPublisher events;
    private final Clock clock;

    CorrectExpenseUseCase(ExpenseAccess expenseAccess, ExpenseRules rules, ExpenseRepository expenses,
                          DomainEventPublisher events, Clock clock) {
        this.expenseAccess = expenseAccess;
        this.rules = rules;
        this.expenses = expenses;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public ExpenseView correct(CorrectExpenseCommand command) {
        var expense = expenseAccess.loadForMember(command.householdId(), command.expenseId(), command.requesterId());
        var current = expense.details().projectId();
        expense.correct(rules.validDetails(expense.householdId(), command.input(), current), clock);
        expenses.save(expense);
        events.publishEventsOf(expense);
        return ExpenseView.from(expense);
    }
}
