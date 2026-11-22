package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.WithdrawLinkedExpense;
import io.github.doriangrelu.lifemanager.budget.application.port.out.ChargeExpenseLinks;
import io.github.doriangrelu.lifemanager.budget.application.port.out.ExpenseRepository;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.UUID;

/**
 * Supprime la dépense liée à une source supprimée ailleurs (opération d'entretien). Sans effet si la dépense
 * a déjà été supprimée par un membre, ou si l'événement arrive deux fois.
 */
@Singleton
class WithdrawLinkedExpenseUseCase implements WithdrawLinkedExpense {

    private final ChargeExpenseLinks links;
    private final ExpenseRepository expenses;
    private final DomainEventPublisher events;
    private final Clock clock;

    WithdrawLinkedExpenseUseCase(ChargeExpenseLinks links, ExpenseRepository expenses, DomainEventPublisher events,
                                 Clock clock) {
        this.links = links;
        this.expenses = expenses;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public void withdraw(UUID sourceId, UUID householdId) {
        var household = new HouseholdId(householdId);
        links.expenseOf(sourceId)
                .flatMap(expenses::findById)
                .filter(expense -> expense.householdId().equals(household))
                .ifPresent(expense -> {
                    expense.markDeleted(clock);
                    expenses.delete(expense);
                    events.publishEventsOf(expense);
                });
    }
}
