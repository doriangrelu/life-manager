package io.github.doriangrelu.lifemanager.kernel.domain.event;

import java.time.Instant;

/**
 * Fait métier qui s'est produit dans un agrégat (ex. « foyer créé »).
 * Interne au domaine : les autres domaines reçoivent une version « contrat » de l'événement.
 */
public interface DomainEvent {

    /** Moment où le fait s'est produit. */
    Instant occurredAt();
}
