package io.github.doriangrelu.lifemanager.bills.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.bills.contract.event.ChargeDeadlinesChangedV1;
import io.github.doriangrelu.lifemanager.bills.contract.event.ChargePaidV1;
import io.github.doriangrelu.lifemanager.bills.domain.event.ChargeDeadlineChanged;
import io.github.doriangrelu.lifemanager.bills.domain.event.OccurrencePaid;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox.Outbox;
import io.micronaut.transaction.annotation.TransactionalEventListener;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * Traduit les événements internes des charges en événements de contrat pour les autres domaines.
 * Ils sont écrits dans la boîte d'envoi avec le changement (même transaction), puis distribués.
 */
@Singleton
class BillsContractEventRelay {

    private final Outbox outbox;

    BillsContractEventRelay(@Named("bills") Outbox outbox) {
        this.outbox = outbox;
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "bills")
    void on(OccurrencePaid event) {
        var charge = event.charge();
        var beneficiary = charge.beneficiary();
        outbox.record(new ChargePaidV1(event.occurrenceId().value(), event.householdId().value(),
                charge.name().value(), event.paid().cents(), event.paidOn(), charge.budgetCategoryId(),
                charge.payer().value(), beneficiary == null ? null : beneficiary.value(), event.occurredAt(),
                charge.direction().name()));
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "bills")
    void on(ChargeDeadlineChanged event) {
        outbox.record(new ChargeDeadlinesChangedV1(event.chargeId().value(), event.householdId().value(),
                event.name().value(), event.nextDueOn(), event.awaitingConfirmation(), event.occurredAt()));
    }
}
