package io.github.doriangrelu.lifemanager.identity.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.identity.contract.event.UserDeletedV1;
import io.github.doriangrelu.lifemanager.identity.contract.event.UserProfileChangedV1;
import io.github.doriangrelu.lifemanager.identity.contract.event.UserRegisteredV1;
import io.github.doriangrelu.lifemanager.identity.domain.event.UserDeleted;
import io.github.doriangrelu.lifemanager.identity.domain.event.UserProfileChanged;
import io.github.doriangrelu.lifemanager.identity.domain.event.UserRegistered;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox.Outbox;
import io.micronaut.transaction.annotation.TransactionalEventListener;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * Traduit les événements internes d'identity en événements de contrat pour les autres domaines.
 * Ils sont écrits dans la boîte d'envoi avec le changement (même transaction), puis distribués :
 * un domaine ne réagit jamais à un changement annulé, et n'en manque aucun validé.
 */
@Singleton
class IdentityContractEventRelay {

    private final Outbox outbox;

    IdentityContractEventRelay(@Named("identity") Outbox outbox) {
        this.outbox = outbox;
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "identity")
    void on(UserRegistered event) {
        outbox.record(new UserRegisteredV1(event.userId().value(), event.email().value(),
                event.displayName().value(), event.occurredAt()));
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "identity")
    void on(UserProfileChanged event) {
        outbox.record(new UserProfileChangedV1(event.userId().value(), event.email().value(),
                event.displayName().value(), event.occurredAt()));
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.BEFORE_COMMIT,
            transactionManager = "identity")
    void on(UserDeleted event) {
        outbox.record(new UserDeletedV1(event.userId().value(), event.occurredAt()));
    }
}
