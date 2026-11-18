package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'une pièce jointe du carnet d'entretien. */
public record AttachmentId(UUID value) {

    public AttachmentId {
        Require.notNull(value, "L'identifiant de la pièce jointe");
    }

    public static AttachmentId newId() {
        return new AttachmentId(UUID.randomUUID());
    }
}
