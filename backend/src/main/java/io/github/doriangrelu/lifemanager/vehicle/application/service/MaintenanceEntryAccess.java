package io.github.doriangrelu.lifemanager.vehicle.application.service;

import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceAttachmentRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceEntryRepository;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceAttachment;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceEntry;
import io.github.doriangrelu.lifemanager.vehicle.domain.exception.AttachmentNotFoundException;
import io.github.doriangrelu.lifemanager.vehicle.domain.exception.MaintenanceEntryNotFoundException;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.AttachmentId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Cloisonnement du carnet : une opération et ses pièces jointes ne sont visibles que des membres du foyer. */
@Singleton
public class MaintenanceEntryAccess {

    private final VehicleAccess vehicles;
    private final MaintenanceEntryRepository entries;
    private final MaintenanceAttachmentRepository attachments;

    MaintenanceEntryAccess(VehicleAccess vehicles, MaintenanceEntryRepository entries,
                           MaintenanceAttachmentRepository attachments) {
        this.vehicles = vehicles;
        this.entries = entries;
        this.attachments = attachments;
    }

    /** @throws MaintenanceEntryNotFoundException si l'opération n'existe pas pour ce véhicule du foyer */
    public MaintenanceEntry entryForMember(UUID householdId, UUID vehicleId, UUID entryId, String requesterId) {
        var vehicle = vehicles.loadForMember(householdId, vehicleId, requesterId);
        return entries.findById(new MaintenanceEntryId(entryId))
                .filter(entry -> entry.belongsTo(vehicle.id()))
                .orElseThrow(MaintenanceEntryNotFoundException::new);
    }

    /** @throws AttachmentNotFoundException si la pièce jointe n'existe pas pour cette opération */
    public MaintenanceAttachment attachmentForMember(UUID householdId, UUID vehicleId, UUID entryId,
                                                     UUID attachmentId, String requesterId) {
        var entry = entryForMember(householdId, vehicleId, entryId, requesterId);
        return attachments.findById(new AttachmentId(attachmentId))
                .filter(attachment -> attachment.belongsTo(entry.id()))
                .orElseThrow(AttachmentNotFoundException::new);
    }
}
