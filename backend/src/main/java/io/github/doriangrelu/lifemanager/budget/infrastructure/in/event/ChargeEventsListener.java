package io.github.doriangrelu.lifemanager.budget.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.bills.contract.event.ChargePaidV1;
import io.github.doriangrelu.lifemanager.budget.application.port.in.RecordChargeExpense;
import io.github.doriangrelu.lifemanager.budget.application.port.in.RecordChargeIncome;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordChargeExpenseCommand;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/** Écoute les échéances de charges payées : chacune devient une dépense, ou une rentrée selon son sens. */
@Singleton
class ChargeEventsListener {

    private static final String INCOME = "INCOME";

    private final RecordChargeExpense recordChargeExpense;
    private final RecordChargeIncome recordChargeIncome;

    ChargeEventsListener(RecordChargeExpense recordChargeExpense, RecordChargeIncome recordChargeIncome) {
        this.recordChargeExpense = recordChargeExpense;
        this.recordChargeIncome = recordChargeIncome;
    }

    @EventListener
    void on(ChargePaidV1 event) {
        var command = new RecordChargeExpenseCommand(event.occurrenceId(), event.householdId(),
                event.chargeName(), event.amountCents(), event.paidOn(), event.budgetCategoryId(), event.payerId(),
                event.beneficiaryId());
        // Sens absent (message d'avant les rentrées) = dépense.
        if (INCOME.equals(event.direction())) {
            recordChargeIncome.record(command);
        } else {
            recordChargeExpense.record(command);
        }
    }
}
