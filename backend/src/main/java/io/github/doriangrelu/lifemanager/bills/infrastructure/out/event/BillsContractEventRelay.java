package io.github.doriangrelu.lifemanager.bills.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.bills.contract.event.ChargeDeadlinesChangedV1;
import io.github.doriangrelu.lifemanager.bills.contract.event.ChargePaidV1;
import io.github.doriangrelu.lifemanager.bills.domain.event.ChargeDeadlineChanged;
import io.github.doriangrelu.lifemanager.bills.domain.event.OccurrencePaid;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.transaction.annotation.TransactionalEventListener;
import jakarta.inject.Singleton;

/**
 * Traduit les événements internes des charges en événements de contrat pour les autres domaines,
 * une fois la transaction validée. Point de branchement d'un futur broker externe.
 */
@Singleton
class BillsContractEventRelay {

    private final ApplicationEventPublisher<Object> eventBus;

    BillsContractEventRelay(ApplicationEventPublisher<Object> eventBus) {
        this.eventBus = eventBus;
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.AFTER_COMMIT,
            transactionManager = "bills")
    void on(OccurrencePaid event) {
        var charge = event.charge();
        var beneficiary = charge.beneficiary();
        eventBus.publishEvent(new ChargePaidV1(event.occurrenceId().value(), event.householdId().value(),
                charge.name().value(), event.paid().cents(), event.paidOn(), charge.budgetCategoryId(),
                charge.payer().value(), beneficiary == null ? null : beneficiary.value(), event.occurredAt()));
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.AFTER_COMMIT,
            transactionManager = "bills")
    void on(ChargeDeadlineChanged event) {
        eventBus.publishEvent(new ChargeDeadlinesChangedV1(event.chargeId().value(), event.householdId().value(),
                event.name().value(), event.nextDueOn(), event.awaitingConfirmation(), event.occurredAt()));
    }
}
