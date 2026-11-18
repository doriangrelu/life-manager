package io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.GenericRepository;

import java.time.Instant;
import java.util.List;

/**
 * Requêtes communes sur la table {@code outbox}.
 * Chaque domaine en hérite avec son propre dépôt {@code @Repository("<domaine>")} : la table reste dans son schéma.
 */
public interface OutboxJdbcRepository extends GenericRepository<OutboxEntity, Long> {

    OutboxEntity save(OutboxEntity entity);

    /** Prochains événements à distribuer, dans l'ordre d'enregistrement (hors événements mis de côté). */
    @Query("SELECT * FROM outbox WHERE sent_at IS NULL AND attempts < :maxAttempts ORDER BY id LIMIT :limit")
    List<OutboxEntity> findPending(int maxAttempts, int limit);

    @Query("UPDATE outbox SET sent_at = :sentAt WHERE id = :id")
    void markSent(long id, Instant sentAt);

    /** Un essai de plus, avec la raison de l'échec. */
    @Query("UPDATE outbox SET attempts = attempts + 1, last_error = :error WHERE id = :id")
    void markFailed(long id, String error);

    @Query("DELETE FROM outbox WHERE sent_at < :before")
    void deleteSentBefore(Instant before);
}
