package io.github.doriangrelu.lifemanager.budget.application.usecase;

import io.github.doriangrelu.lifemanager.budget.application.port.in.RecordChargeExpense;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordChargeExpenseCommand;
import io.github.doriangrelu.lifemanager.budget.application.port.out.ChargeExpenseLinks;
import io.github.doriangrelu.lifemanager.budget.application.port.out.ExpenseRepository;
import io.github.doriangrelu.lifemanager.budget.application.service.ChargeExpenseDetails;
import io.github.doriangrelu.lifemanager.budget.domain.aggregate.Expense;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

/**
 * Enregistre la dépense d'une échéance de charge payée. L'échéance est réservée avant la création :
 * reçue deux fois, elle ne produit qu'une dépense ; supprimée par un membre, la dépense ne revient pas.
 */
@Singleton
class RecordChargeExpenseUseCase implements RecordChargeExpense {

    private static final Logger LOG = LoggerFactory.getLogger(RecordChargeExpenseUseCase.class);

    private final ChargeExpenseDetails details;
    private final ChargeExpenseLinks links;
    private final ExpenseRepository expenses;
    private final DomainEventPublisher events;
    private final Clock clock;

    RecordChargeExpenseUseCase(ChargeExpenseDetails details, ChargeExpenseLinks links, ExpenseRepository expenses,
                               DomainEventPublisher events, Clock clock) {
        this.details = details;
        this.links = links;
        this.expenses = expenses;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "budget")
    public void record(RecordChargeExpenseCommand command) {
        var expenseDetails = details.of(command);
        if (expenseDetails.isEmpty()) {
            LOG.warn("Échéance {} : pas de dépense (montant nul ou payeur hors du foyer)", command.occurrenceId());
            return;
        }
        var expenseId = ExpenseId.newId();
        if (!links.claim(command.occurrenceId(), expenseId)) {
            return; // dépense déjà créée pour cette échéance
        }
        var paid = expenseDetails.get();
        var expense = Expense.record(expenseId, new HouseholdId(command.householdId()), paid, paid.paidBy(), clock);
        expenses.save(expense);
        events.publishEventsOf(expense);
    }
}
