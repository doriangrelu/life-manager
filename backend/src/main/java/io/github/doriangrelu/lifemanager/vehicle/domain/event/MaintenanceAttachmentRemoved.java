package io.github.doriangrelu.lifemanager.vehicle.domain.event;

import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;
import io.github.doriangrelu.lifemanager.kernel.domain.file.StorageKey;

import java.time.Instant;

/** Une pièce jointe a été supprimée : son fichier peut quitter le disque une fois la transaction validée. */
public record MaintenanceAttachmentRemoved(StorageKey storageKey, Instant occurredAt) implements DomainEvent {
}
