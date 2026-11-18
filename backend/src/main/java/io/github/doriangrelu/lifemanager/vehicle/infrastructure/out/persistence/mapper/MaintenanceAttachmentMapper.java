package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceAttachment;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.AttachedFile;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.AttachmentFileName;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.AttachmentId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.AttachmentMediaType;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.FileSize;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.StorageKey;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity.MaintenanceAttachmentEntity;

/** Conversion entre l'agrégat {@link MaintenanceAttachment} et sa ligne en base. */
public final class MaintenanceAttachmentMapper {

    private MaintenanceAttachmentMapper() {
    }

    public static MaintenanceAttachmentEntity toEntity(MaintenanceAttachment attachment) {
        var file = attachment.file();
        return new MaintenanceAttachmentEntity(attachment.id().value(), attachment.entryId().value(),
                file.fileName().value(), file.mediaType().name(), file.size().bytes(),
                file.storageKey().value(), attachment.addedBy().value(), attachment.addedAt());
    }

    public static MaintenanceAttachment toDomain(MaintenanceAttachmentEntity entity) {
        var file = new AttachedFile(new AttachmentFileName(entity.fileName()),
                AttachmentMediaType.valueOf(entity.mediaType()), new FileSize(entity.sizeBytes()),
                new StorageKey(entity.storageKey()));
        return MaintenanceAttachment.restore(new AttachmentId(entity.id()), new MaintenanceEntryId(entity.entryId()),
                file, new MemberId(entity.addedBy()), entity.addedAt());
    }
}
