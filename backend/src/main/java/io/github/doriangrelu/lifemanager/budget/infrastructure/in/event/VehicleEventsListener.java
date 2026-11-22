package io.github.doriangrelu.lifemanager.budget.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.budget.application.port.in.RecordChargeExpense;
import io.github.doriangrelu.lifemanager.budget.application.port.in.WithdrawLinkedExpense;
import io.github.doriangrelu.lifemanager.budget.application.port.in.command.RecordChargeExpenseCommand;
import io.github.doriangrelu.lifemanager.vehicle.contract.event.MaintenanceExpenseSentV1;
import io.github.doriangrelu.lifemanager.vehicle.contract.event.MaintenanceExpenseWithdrawnV1;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/**
 * Écoute le carnet d'entretien : une opération ajoutée au budget devient une dépense pour tout le foyer
 * (même mécanisme que les échéances de charges : une seule dépense par opération), et part avec elle.
 */
@Singleton
class VehicleEventsListener {

    private final RecordChargeExpense recordExpense;
    private final WithdrawLinkedExpense withdrawExpense;

    VehicleEventsListener(RecordChargeExpense recordExpense, WithdrawLinkedExpense withdrawExpense) {
        this.recordExpense = recordExpense;
        this.withdrawExpense = withdrawExpense;
    }

    @EventListener
    void on(MaintenanceExpenseSentV1 event) {
        recordExpense.record(new RecordChargeExpenseCommand(event.entryId(), event.householdId(), event.label(),
                event.amountCents(), event.performedOn(), event.budgetCategoryId(), event.payerId(), null));
    }

    @EventListener
    void on(MaintenanceExpenseWithdrawnV1 event) {
        withdrawExpense.withdraw(event.entryId(), event.householdId());
    }
}
