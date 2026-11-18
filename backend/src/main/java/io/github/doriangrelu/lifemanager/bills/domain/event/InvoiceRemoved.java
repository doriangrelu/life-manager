package io.github.doriangrelu.lifemanager.bills.domain.event;

import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;
import io.github.doriangrelu.lifemanager.kernel.domain.file.StorageKey;

import java.time.Instant;

/** Une facture a été supprimée : son fichier peut quitter le disque une fois la transaction validée. */
public record InvoiceRemoved(StorageKey storageKey, Instant occurredAt) implements DomainEvent {
}
