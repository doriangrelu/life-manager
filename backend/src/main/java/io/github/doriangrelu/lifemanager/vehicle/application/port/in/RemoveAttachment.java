package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.AttachmentCommand;

/** Supprime une pièce jointe ; son fichier quitte le disque après validation. */
public interface RemoveAttachment {

    void remove(AttachmentCommand command);
}
