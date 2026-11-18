package io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox;

import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.transaction.TransactionDefinition;
import io.micronaut.transaction.support.TransactionSynchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Boîte d'envoi d'un domaine : garantit qu'un événement de contrat validé avec un changement
 * arrive aux autres domaines, même après un arrêt brutal ou une erreur d'un destinataire.
 * <ul>
 *   <li>{@link #record} : écrit l'événement dans la transaction en cours, puis le distribue dès la validation ;</li>
 *   <li>{@link #deliver} : distribue ce qui reste, dans l'ordre (appelé aussi par la relance planifiée).</li>
 * </ul>
 * Un événement peut arriver deux fois (arrêt juste après la distribution) : les destinataires l'acceptent.
 */
public class Outbox {

    /** Au-delà, l'événement est mis de côté (journalisé) pour ne pas bloquer les suivants. */
    static final int MAX_ATTEMPTS = 10;
    private static final int BATCH = 50;
    private static final Duration KEEP_SENT = Duration.ofDays(7);
    private static final TransactionDefinition NEW = TransactionDefinition.of(TransactionDefinition.Propagation.REQUIRES_NEW);
    private static final Logger LOG = LoggerFactory.getLogger(Outbox.class);

    private final OutboxStorage storage;
    private final OutboxCodec codec;
    private final ApplicationEventPublisher<Object> eventBus;
    private final Clock clock;
    // Une seule distribution à la fois : l'ordre d'enregistrement est respecté.
    private final ReentrantLock delivering = new ReentrantLock();

    public Outbox(OutboxStorage storage, OutboxCodec codec, ApplicationEventPublisher<Object> eventBus, Clock clock) {
        this.storage = storage;
        this.codec = codec;
        this.eventBus = eventBus;
        this.clock = clock;
    }

    /** Enregistre l'événement avec le changement en cours ; il part dès que la transaction est validée. */
    public void record(Object event) {
        var transaction = storage.transactions().findTransactionStatus()
                .orElseThrow(() -> new IllegalStateException("Événement hors transaction : " + codec.typeOf(event)));
        storage.table().save(OutboxEntity.pending(codec.typeOf(event), codec.encode(event), clock.instant()));
        transaction.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deliver();
            }
        });
    }

    /** Distribue les événements en attente ; s'arrête au premier échec pour garder l'ordre. */
    public void deliver() {
        delivering.lock();
        try {
            List<OutboxEntity> pending;
            do {
                pending = inNewTransaction(table -> table.findPending(MAX_ATTEMPTS, BATCH));
                for (var message : pending) {
                    if (!deliverOne(message)) {
                        return;
                    }
                }
            } while (pending.size() == BATCH);
        } finally {
            delivering.unlock();
        }
    }

    /** Supprime les événements distribués depuis plus de 7 jours. */
    public void purge() {
        var before = clock.instant().minus(KEEP_SENT);
        run(table -> table.deleteSentBefore(before));
    }

    String domain() {
        return storage.domain();
    }

    private boolean deliverOne(OutboxEntity message) {
        try {
            run(table -> {
                eventBus.publishEvent(codec.decode(message.type(), message.payload()));
                table.markSent(message.id(), clock.instant());
            });
            return true;
        } catch (RuntimeException e) {
            fail(message, e);
            return false;
        }
    }

    private void fail(OutboxEntity message, RuntimeException error) {
        var reason = String.valueOf(error.getMessage());
        run(table -> table.markFailed(message.id(), reason.substring(0, Math.min(reason.length(), 1000))));
        if (message.attempts() + 1 >= MAX_ATTEMPTS) {
            LOG.error("Événement {} n°{} du domaine {} mis de côté après {} échecs", message.type(), message.id(),
                    domain(), MAX_ATTEMPTS, error);
        } else {
            LOG.warn("Événement {} n°{} du domaine {} refusé, nouvel essai plus tard : {}", message.type(),
                    message.id(), domain(), reason);
        }
    }

    private void run(Consumer<OutboxJdbcRepository> work) {
        inNewTransaction(table -> {
            work.accept(table);
            return null;
        });
    }

    // Transaction neuve : appelée aussi juste après la validation d'une autre, qui ne doit pas être réutilisée.
    private <R> R inNewTransaction(Function<OutboxJdbcRepository, R> work) {
        return storage.transactions().execute(NEW, status -> work.apply(storage.table()));
    }
}
