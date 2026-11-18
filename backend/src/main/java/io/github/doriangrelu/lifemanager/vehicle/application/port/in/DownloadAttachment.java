package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.AttachmentCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.AttachmentContent;

/** Donne le contenu d'une pièce jointe à un membre du foyer. */
public interface DownloadAttachment {

    AttachmentContent download(AttachmentCommand command);
}
