package io.github.doriangrelu.lifemanager.vehicle.application.service;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.AttachFileCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.AttachmentView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceAttachmentRepository;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceAttachment;
import io.github.doriangrelu.lifemanager.vehicle.domain.exception.TooManyAttachmentsException;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachedFile;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.AttachmentId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Partie en base de l'ajout d'une pièce jointe, dans sa propre transaction (le fichier est déjà écrit). */
@Singleton
public class AttachmentRecorder {

    private final MaintenanceEntryAccess access;
    private final MaintenanceAttachmentRepository attachments;
    private final Clock clock;

    AttachmentRecorder(MaintenanceEntryAccess access, MaintenanceAttachmentRepository attachments, Clock clock) {
        this.access = access;
        this.attachments = attachments;
        this.clock = clock;
    }

    /** Vérifie, avant d'écrire le moindre fichier, que le demandeur voit cette opération (sinon 404). */
    @Transactional(transactionManager = "vehicle", readOnly = true)
    public void checkAccess(AttachFileCommand command) {
        access.entryForMember(command.householdId(), command.vehicleId(), command.entryId(), command.requesterId());
    }

    /** @throws TooManyAttachmentsException si l'opération a déjà son maximum de pièces jointes */
    @Transactional(transactionManager = "vehicle")
    public AttachmentView record(AttachFileCommand command, AttachedFile file) {
        var entry = access.entryForMember(command.householdId(), command.vehicleId(), command.entryId(),
                command.requesterId());
        if (attachments.countByEntry(entry.id()) >= MaintenanceAttachment.MAX_PER_ENTRY) {
            throw new TooManyAttachmentsException(MaintenanceAttachment.MAX_PER_ENTRY);
        }
        var attachment = MaintenanceAttachment.attach(AttachmentId.newId(), entry.id(), file,
                MemberId.of(command.requesterId()), clock);
        attachments.save(attachment);
        return AttachmentView.from(attachment);
    }
}
