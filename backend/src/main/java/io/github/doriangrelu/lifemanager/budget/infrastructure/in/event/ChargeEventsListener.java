package io.github.doriangrelu.lifemanager.budget.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.bills.contract.event.ChargePaidV1;
import io.github.doriangrelu.lifemanager.budget.application.port.in.RecordChargeExpense;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordChargeExpenseCommand;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/** Écoute les échéances de charges payées : chacune devient une dépense du budget. */
@Singleton
class ChargeEventsListener {

    private final RecordChargeExpense recordChargeExpense;

    ChargeEventsListener(RecordChargeExpense recordChargeExpense) {
        this.recordChargeExpense = recordChargeExpense;
    }

    @EventListener
    void on(ChargePaidV1 event) {
        recordChargeExpense.record(new RecordChargeExpenseCommand(event.occurrenceId(), event.householdId(),
                event.chargeName(), event.amountCents(), event.paidOn(), event.budgetCategoryId(), event.payerId(),
                event.beneficiaryId()));
    }
}
