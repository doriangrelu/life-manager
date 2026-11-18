package io.github.doriangrelu.lifemanager.vehicle.application.port.in.result;

import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceAttachment;

import java.time.Instant;
import java.util.UUID;

/** Une pièce jointe telle que vue hors du domaine ; {@code mediaType} est le type MIME vérifié. */
public record AttachmentView(UUID id, String fileName, String mediaType, long sizeBytes, Instant addedAt) {

    public static AttachmentView from(MaintenanceAttachment attachment) {
        var file = attachment.file();
        return new AttachmentView(attachment.id().value(), file.fileName().value(), file.mediaType().mimeType(),
                file.size().bytes(), attachment.addedAt());
    }
}
