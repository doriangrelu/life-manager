package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.AttachFileCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.AttachmentView;

/** Joint un fichier (PDF, JPEG, PNG ou WebP, 10 Mo au plus) à une opération du carnet. */
public interface AttachFile {

    AttachmentView attach(AttachFileCommand command);
}
