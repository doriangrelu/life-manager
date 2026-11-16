package io.github.doriangrelu.lifemanager.household.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.household.contract.event.HouseholdCreatedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.MemberJoinedV1;
import io.github.doriangrelu.lifemanager.household.domain.event.HouseholdCreated;
import io.github.doriangrelu.lifemanager.household.domain.event.MemberJoined;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.transaction.annotation.TransactionalEventListener;
import jakarta.inject.Singleton;

/**
 * Traduit les événements internes du foyer en événements de contrat pour les autres domaines,
 * une fois la transaction validée. Point de branchement d'un futur broker externe.
 */
@Singleton
class HouseholdContractEventRelay {

    private final ApplicationEventPublisher<Object> eventBus;

    HouseholdContractEventRelay(ApplicationEventPublisher<Object> eventBus) {
        this.eventBus = eventBus;
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.AFTER_COMMIT,
            transactionManager = "household")
    void on(HouseholdCreated event) {
        eventBus.publishEvent(new HouseholdCreatedV1(event.householdId().value(), event.founder().value(),
                event.occurredAt()));
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.AFTER_COMMIT,
            transactionManager = "household")
    void on(MemberJoined event) {
        eventBus.publishEvent(new MemberJoinedV1(event.householdId().value(), event.member().value(),
                event.occurredAt()));
    }
}
