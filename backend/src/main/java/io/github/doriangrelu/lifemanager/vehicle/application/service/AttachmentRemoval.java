package io.github.doriangrelu.lifemanager.vehicle.application.service;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceAttachmentRepository;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceAttachment;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.List;

/**
 * Supprime des pièces jointes dans la transaction en cours ; leurs fichiers quittent le disque après validation
 * (un fichier orphelin vaut mieux qu'une ligne qui pointe dans le vide).
 */
@Singleton
public class AttachmentRemoval {

    private final MaintenanceAttachmentRepository attachments;
    private final DomainEventPublisher events;
    private final Clock clock;

    AttachmentRemoval(MaintenanceAttachmentRepository attachments, DomainEventPublisher events, Clock clock) {
        this.attachments = attachments;
        this.events = events;
        this.clock = clock;
    }

    public void removeAll(List<MaintenanceAttachment> toRemove) {
        toRemove.forEach(this::remove);
    }

    public void remove(MaintenanceAttachment attachment) {
        attachment.markRemoved(clock);
        attachments.delete(attachment);
        events.publishEventsOf(attachment);
    }
}
