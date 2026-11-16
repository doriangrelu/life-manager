package io.github.doriangrelu.lifemanager.identity.infrastructure.out.event;

import io.github.doriangrelu.lifemanager.identity.contract.event.UserProfileChangedV1;
import io.github.doriangrelu.lifemanager.identity.contract.event.UserRegisteredV1;
import io.github.doriangrelu.lifemanager.identity.domain.event.UserProfileChanged;
import io.github.doriangrelu.lifemanager.identity.domain.event.UserRegistered;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.transaction.annotation.TransactionalEventListener;
import jakarta.inject.Singleton;

/**
 * Traduit les événements internes d'identity en événements de contrat et les publie pour les autres domaines,
 * une fois la transaction validée : un domaine ne réagit jamais à un changement annulé.
 * C'est ce composant qui publiera vers un broker externe le jour où les domaines seront séparés.
 */
@Singleton
class IdentityContractEventRelay {

    private final ApplicationEventPublisher<Object> eventBus;

    IdentityContractEventRelay(ApplicationEventPublisher<Object> eventBus) {
        this.eventBus = eventBus;
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.AFTER_COMMIT,
            transactionManager = "identity")
    void on(UserRegistered event) {
        eventBus.publishEvent(new UserRegisteredV1(event.userId().value(), event.email().value(),
                event.displayName().value(), event.occurredAt()));
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.AFTER_COMMIT,
            transactionManager = "identity")
    void on(UserProfileChanged event) {
        eventBus.publishEvent(new UserProfileChangedV1(event.userId().value(), event.email().value(),
                event.displayName().value(), event.occurredAt()));
    }
}
