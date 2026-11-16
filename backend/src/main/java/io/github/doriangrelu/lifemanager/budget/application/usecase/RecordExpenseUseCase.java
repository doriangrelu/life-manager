package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.RecordExpense;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordExpenseCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.in.result.ExpenseView;
import io.github.doriangrelu.lifemanager.budget.application.port.out.ExpenseRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.BudgetAccess;
import io.github.doriangrelu.lifemanager.budget.application.service.ExpenseRules;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Expense;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Enregistre une dépense saisie par un membre du foyer. */
@Singleton
class RecordExpenseUseCase implements RecordExpense {

    private final BudgetAccess access;
    private final ExpenseRules rules;
    private final ExpenseRepository expenses;
    private final DomainEventPublisher events;
    private final Clock clock;

    RecordExpenseUseCase(BudgetAccess access, ExpenseRules rules, ExpenseRepository expenses,
                         DomainEventPublisher events, Clock clock) {
        this.access = access;
        this.rules = rules;
        this.expenses = expenses;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public ExpenseView record(RecordExpenseCommand command) {
        var household = access.householdOfMember(command.householdId(), command.requesterId());
        var details = rules.validDetails(household, command.input());
        var expense = Expense.record(ExpenseId.newId(), household, details, MemberId.of(command.requesterId()), clock);
        expenses.save(expense);
        events.publishEventsOf(expense);
        return ExpenseView.from(expense);
    }
}
