package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.DeleteMaintenanceEntry;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.DeleteMaintenanceEntryCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceAttachmentRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceEntryRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.AttachmentRemoval;
import io.github.doriangrelu.lifemanager.vehicle.application.service.MaintenanceEntryAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Supprime définitivement une opération du carnet d'un véhicule du foyer, avec ses pièces jointes. */
@Singleton
class DeleteMaintenanceEntryUseCase implements DeleteMaintenanceEntry {

    private final MaintenanceEntryAccess access;
    private final MaintenanceEntryRepository entries;
    private final MaintenanceAttachmentRepository attachments;
    private final AttachmentRemoval removal;

    DeleteMaintenanceEntryUseCase(MaintenanceEntryAccess access, MaintenanceEntryRepository entries,
                                  MaintenanceAttachmentRepository attachments, AttachmentRemoval removal) {
        this.access = access;
        this.entries = entries;
        this.attachments = attachments;
        this.removal = removal;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public void delete(DeleteMaintenanceEntryCommand command) {
        var entry = access.entryForMember(command.householdId(), command.vehicleId(), command.entryId(),
                command.requesterId());
        removal.removeAll(attachments.findAllByEntry(entry.id()));
        entries.delete(entry);
    }
}
