package io.github.doriangrelu.lifemanager.bills.infrastructure.out.storage;

import io.github.doriangrelu.lifemanager.bills.application.port.out.InvoiceStorage;
import io.github.doriangrelu.lifemanager.bills.domain.event.InvoiceRemoved;
import io.micronaut.transaction.annotation.TransactionalEventListener;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;

/** Supprime le fichier d'une facture une fois sa suppression en base validée. */
@Singleton
class InvoiceFileCleaner {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceFileCleaner.class);

    private final InvoiceStorage storage;

    InvoiceFileCleaner(InvoiceStorage storage) {
        this.storage = storage;
    }

    @TransactionalEventListener(value = TransactionalEventListener.TransactionPhase.AFTER_COMMIT,
            transactionManager = "bills")
    void on(InvoiceRemoved event) {
        try {
            storage.delete(event.storageKey());
        } catch (UncheckedIOException e) {
            // Fichier orphelin : sans gravité pour l'utilisateur, à nettoyer à la main.
            LOG.warn("Fichier de facture {} non supprimé", event.storageKey().value(), e);
        }
    }
}
