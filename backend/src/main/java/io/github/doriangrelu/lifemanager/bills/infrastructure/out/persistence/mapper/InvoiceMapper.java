package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.bills.domain.aggregate.Invoice;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.InvoiceId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceId;
import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.entity.InvoiceEntity;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachedFile;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachmentFileName;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachmentMediaType;
import io.github.doriangrelu.lifemanager.kernel.domain.file.FileSize;
import io.github.doriangrelu.lifemanager.kernel.domain.file.StorageKey;

/** Conversion entre l'agrégat {@link Invoice} et sa ligne en base. */
public final class InvoiceMapper {

    private InvoiceMapper() {
    }

    public static InvoiceEntity toEntity(Invoice invoice) {
        var file = invoice.file();
        return new InvoiceEntity(invoice.id().value(), invoice.occurrenceId().value(), file.fileName().value(),
                file.mediaType().name(), file.size().bytes(), file.storageKey().value(), invoice.addedBy().value(),
                invoice.addedAt());
    }

    public static Invoice toDomain(InvoiceEntity entity) {
        var file = new AttachedFile(new AttachmentFileName(entity.fileName()),
                AttachmentMediaType.valueOf(entity.mediaType()), new FileSize(entity.sizeBytes()),
                new StorageKey(entity.storageKey()));
        return Invoice.restore(new InvoiceId(entity.id()), new OccurrenceId(entity.occurrenceId()), file,
                new MemberId(entity.addedBy()), entity.addedAt());
    }
}
