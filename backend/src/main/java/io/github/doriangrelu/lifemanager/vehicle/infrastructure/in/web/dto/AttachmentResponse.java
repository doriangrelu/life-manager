package io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.AttachmentView;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

/** Une pièce jointe du carnet ; {@code mediaType} est le type MIME vérifié. */
@Serdeable
public record AttachmentResponse(UUID id, String fileName, String mediaType, long sizeBytes, Instant addedAt) {

    public static AttachmentResponse from(AttachmentView attachment) {
        return new AttachmentResponse(attachment.id(), attachment.fileName(), attachment.mediaType(),
                attachment.sizeBytes(), attachment.addedAt());
    }
}
