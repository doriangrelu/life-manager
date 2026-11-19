package io.github.doriangrelu.lifemanager.household.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.household.contract.event.HouseholdCreatedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.HouseholdDeletedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.MemberJoinedV1;
import io.github.doriangrelu.lifemanager.household.contract.event.MemberLeftV1;
import io.github.doriangrelu.lifemanager.household.domain.event.HouseholdCreated;
import io.github.doriangrelu.lifemanager.household.domain.event.HouseholdDeleted;
import io.github.doriangrelu.lifemanager.household.domain.event.MemberJoined;
import io.github.doriangrelu.lifemanager.household.domain.event.MemberLeft;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox.Outbox;
import io.micronaut.transaction.annotation.TransactionalEventListener;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * Traduit les événements internes du foyer en événements de contrat pour les autres domaines.
 * Ils sont écrits dans la boîte d'envoi avec le changement (même transaction), puis distribués.
 */
@Singleton
class HouseholdContractEventRelay {

    private final Outbox outbox;

    HouseholdContractEventRelay(@Named("household") Outbox outbox) {
        this.outbox = outbox;
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "household")
    void on(HouseholdCreated event) {
        outbox.record(new HouseholdCreatedV1(event.householdId().value(), event.founder().value(),
                event.occurredAt()));
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "household")
    void on(MemberLeft event) {
        outbox.record(new MemberLeftV1(event.householdId().value(), event.member().value(), event.owner().value(),
                event.occurredAt()));
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "household")
    void on(HouseholdDeleted event) {
        outbox.record(new HouseholdDeletedV1(event.householdId().value(), event.occurredAt()));
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "household")
    void on(MemberJoined event) {
        outbox.record(new MemberJoinedV1(event.householdId().value(), event.member().value(),
                event.occurredAt()));
    }
}
