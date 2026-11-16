package io.github.doriangrelu.lifemanager.kernel.application.port.out;

import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;

/**
 * Publie les événements métier d'un agrégat sur le bus interne.
 * Le use case l'appelle juste après avoir sauvegardé l'agrégat.
 */
public interface DomainEventPublisher {

    /** Publie puis vide les événements en attente de l'agrégat. */
    void publishEventsOf(AggregateRoot<?> aggregate);
}
