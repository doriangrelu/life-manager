package io.github.doriangrelu.lifemanager.kernel.infrastructure.event;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Singleton;

/**
 * Publie les événements métier sur le bus d'événements de Micronaut (dans le même processus).
 * Les écouteurs reçoivent l'événement de façon synchrone, dans la transaction en cours.
 */
@Singleton
class MicronautDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher<Object> eventBus;

    MicronautDomainEventPublisher(ApplicationEventPublisher<Object> eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void publishEventsOf(AggregateRoot<?> aggregate) {
        aggregate.pullEvents().forEach(eventBus::publishEvent);
    }
}
