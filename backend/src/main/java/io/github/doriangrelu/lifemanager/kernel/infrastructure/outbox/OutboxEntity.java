package io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;

/**
 * Ligne de la table {@code outbox} d'un domaine : un événement de contrat à distribuer.
 * {@code id} croissant donne l'ordre d'envoi ; {@code sentAt} vide = pas encore distribué.
 */
@MappedEntity("outbox")
public record OutboxEntity(@Id @GeneratedValue @Nullable Long id, String type, String payload, Instant createdAt,
                           @Nullable Instant sentAt, int attempts, @Nullable String lastError) {

    /** Nouvel événement, pas encore distribué. */
    static OutboxEntity pending(String type, String payload, Instant now) {
        return new OutboxEntity(null, type, payload, now, null, 0, null);
    }
}
