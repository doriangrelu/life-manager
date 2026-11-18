package io.github.doriangrelu.lifemanager.vehicle.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Pièce jointe inexistante pour cette opération du carnet. */
public class AttachmentNotFoundException extends NotFoundException {

    public AttachmentNotFoundException() {
        super("Pièce jointe introuvable");
    }
}
