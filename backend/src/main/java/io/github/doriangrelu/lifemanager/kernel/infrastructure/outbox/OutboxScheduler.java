package io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox;

import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

/**
 * Relance des boîtes d'envoi de tous les domaines : au démarrage (événements restés après un arrêt),
 * toutes les 5 secondes (après une erreur) et ménage chaque nuit.
 */
@Singleton
class OutboxScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(OutboxScheduler.class);

    private final List<Outbox> outboxes;

    OutboxScheduler(List<Outbox> outboxes) {
        this.outboxes = outboxes;
    }

    @EventListener
    void onStartup(ServerStartupEvent event) {
        each(Outbox::deliver);
    }

    @Scheduled(fixedDelay = "5s", initialDelay = "5s")
    void retry() {
        each(Outbox::deliver);
    }

    @Scheduled(cron = "0 30 4 * * *")
    void purge() {
        each(Outbox::purge);
    }

    // Un domaine en panne (base injoignable…) n'empêche pas les autres de distribuer.
    private void each(Consumer<Outbox> work) {
        for (var outbox : outboxes) {
            try {
                work.accept(outbox);
            } catch (RuntimeException e) {
                LOG.warn("Boîte d'envoi du domaine {} indisponible : {}", outbox.domain(), e.getMessage());
            }
        }
    }
}
