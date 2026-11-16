package io.github.doriangrelu.lifemanager.kernel.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.entity.Entity;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Racine d'agrégat : seule porte d'entrée pour modifier l'agrégat.
 * Elle garde en mémoire les événements métier produits par ses méthodes,
 * que le use case publie une fois l'agrégat sauvegardé.
 *
 * @param <I> type de l'identifiant
 */
public abstract class AggregateRoot<I> extends Entity<I> {

    private final List<DomainEvent> pendingEvents = new ArrayList<>();

    protected AggregateRoot(I id) {
        super(id);
    }

    /** À appeler par les méthodes métier de l'agrégat quand quelque chose s'est produit. */
    protected void recordEvent(DomainEvent event) {
        pendingEvents.add(Objects.requireNonNull(event, "event"));
    }

    /** Retourne les événements en attente et les retire : chaque événement n'est publié qu'une fois. */
    public List<DomainEvent> pullEvents() {
        var events = List.copyOf(pendingEvents);
        pendingEvents.clear();
        return events;
    }
}
