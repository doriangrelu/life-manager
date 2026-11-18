package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.ListAttachments;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.query.ListAttachmentsQuery;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.AttachmentView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceAttachmentRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.MaintenanceEntryAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Donne les pièces jointes d'une opération du carnet à un membre du foyer. */
@Singleton
class ListAttachmentsUseCase implements ListAttachments {

    private final MaintenanceEntryAccess access;
    private final MaintenanceAttachmentRepository attachments;

    ListAttachmentsUseCase(MaintenanceEntryAccess access, MaintenanceAttachmentRepository attachments) {
        this.access = access;
        this.attachments = attachments;
    }

    @Override
    @Transactional(transactionManager = "vehicle", readOnly = true)
    public List<AttachmentView> list(ListAttachmentsQuery query) {
        var entry = access.entryForMember(query.householdId(), query.vehicleId(), query.entryId(),
                query.requesterId());
        return attachments.findAllByEntry(entry.id()).stream().map(AttachmentView::from).toList();
    }
}
