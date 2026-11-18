package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.DownloadAttachment;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.AttachmentCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.AttachmentContent;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.AttachmentStorage;
import io.github.doriangrelu.lifemanager.vehicle.application.service.MaintenanceEntryAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Donne le contenu d'une pièce jointe à un membre du foyer. */
@Singleton
class DownloadAttachmentUseCase implements DownloadAttachment {

    private final MaintenanceEntryAccess access;
    private final AttachmentStorage storage;

    DownloadAttachmentUseCase(MaintenanceEntryAccess access, AttachmentStorage storage) {
        this.access = access;
        this.storage = storage;
    }

    @Override
    @Transactional(transactionManager = "vehicle", readOnly = true)
    public AttachmentContent download(AttachmentCommand command) {
        var attachment = access.attachmentForMember(command.householdId(), command.vehicleId(), command.entryId(),
                command.attachmentId(), command.requesterId());
        var file = attachment.file();
        return new AttachmentContent(file.fileName().value(), file.mediaType().mimeType(), file.size().bytes(),
                storage.open(file.storageKey()));
    }
}
