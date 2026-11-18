package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.storage;

import io.github.doriangrelu.lifemanager.vehicle.application.port.out.AttachmentStorage;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.MaintenanceAttachmentRemoved;
import io.micronaut.transaction.annotation.TransactionalEventListener;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;

/** Supprime le fichier d'une pièce jointe une fois sa suppression en base validée. */
@Singleton
class AttachmentFileCleaner {

    private static final Logger LOG = LoggerFactory.getLogger(AttachmentFileCleaner.class);

    private final AttachmentStorage storage;

    AttachmentFileCleaner(AttachmentStorage storage) {
        this.storage = storage;
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.AFTER_COMMIT,
            transactionManager = "vehicle")
    void on(MaintenanceAttachmentRemoved event) {
        try {
            storage.delete(event.storageKey());
        } catch (UncheckedIOException e) {
            // Fichier orphelin : sans gravité pour l'utilisateur, à nettoyer à la main.
            LOG.warn("Fichier de pièce jointe {} non supprimé", event.storageKey().value(), e);
        }
    }
}
