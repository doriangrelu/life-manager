package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.AttachInvoice;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.AttachInvoiceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.InvoiceView;
import io.github.doriangrelu.lifemanager.bills.application.port.out.InvoiceStorage;
import io.github.doriangrelu.lifemanager.bills.application.service.InvoiceRecorder;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.ContentTypeDetector;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachedFile;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachmentFileName;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachmentMediaType;
import io.github.doriangrelu.lifemanager.kernel.domain.file.FileSize;
import io.github.doriangrelu.lifemanager.kernel.domain.file.StorageKey;
import jakarta.inject.Singleton;

/**
 * Joint une facture à une échéance. Le fichier est vérifié puis écrit avant la transaction ;
 * si l'enregistrement en base échoue, il est aussitôt supprimé : jamais de fichier sans ligne.
 */
@Singleton
class AttachInvoiceUseCase implements AttachInvoice {

    private final ContentTypeDetector detector;
    private final InvoiceStorage storage;
    private final InvoiceRecorder recorder;

    AttachInvoiceUseCase(ContentTypeDetector detector, InvoiceStorage storage, InvoiceRecorder recorder) {
        this.detector = detector;
        this.storage = storage;
        this.recorder = recorder;
    }

    @Override
    public InvoiceView attach(AttachInvoiceCommand command) {
        // Un non-membre est refusé avant toute écriture sur le disque.
        recorder.checkAccess(command);
        var content = command.content();
        var file = new AttachedFile(AttachmentFileName.clean(command.fileName()),
                AttachmentMediaType.ofMimeType(detector.detect(content)), new FileSize(content.length),
                StorageKey.newKey());
        storage.store(file.storageKey(), content);
        try {
            return recorder.record(command, file);
        } catch (RuntimeException e) {
            storage.delete(file.storageKey());
            throw e;
        }
    }
}
