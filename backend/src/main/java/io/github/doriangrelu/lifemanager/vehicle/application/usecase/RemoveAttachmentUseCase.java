package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.RemoveAttachment;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.AttachmentCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.service.AttachmentRemoval;
import io.github.doriangrelu.lifemanager.vehicle.application.service.MaintenanceEntryAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Supprime une pièce jointe d'une opération du carnet. */
@Singleton
class RemoveAttachmentUseCase implements RemoveAttachment {

    private final MaintenanceEntryAccess access;
    private final AttachmentRemoval removal;

    RemoveAttachmentUseCase(MaintenanceEntryAccess access, AttachmentRemoval removal) {
        this.access = access;
        this.removal = removal;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public void remove(AttachmentCommand command) {
        removal.remove(access.attachmentForMember(command.householdId(), command.vehicleId(), command.entryId(),
                command.attachmentId(), command.requesterId()));
    }
}
