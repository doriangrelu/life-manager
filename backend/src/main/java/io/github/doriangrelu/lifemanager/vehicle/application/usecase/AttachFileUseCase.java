package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.AttachFile;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.AttachFileCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.AttachmentView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.AttachmentStorage;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.ContentTypeDetector;
import io.github.doriangrelu.lifemanager.vehicle.application.service.AttachmentRecorder;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachedFile;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachmentFileName;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachmentMediaType;
import io.github.doriangrelu.lifemanager.kernel.domain.file.FileSize;
import io.github.doriangrelu.lifemanager.kernel.domain.file.StorageKey;
import jakarta.inject.Singleton;

/**
 * Joint un fichier à une opération du carnet. Le fichier est vérifié puis écrit avant la transaction ;
 * si l'enregistrement en base échoue, il est aussitôt supprimé : jamais de fichier sans ligne.
 */
@Singleton
class AttachFileUseCase implements AttachFile {

    private final ContentTypeDetector detector;
    private final AttachmentStorage storage;
    private final AttachmentRecorder recorder;

    AttachFileUseCase(ContentTypeDetector detector, AttachmentStorage storage, AttachmentRecorder recorder) {
        this.detector = detector;
        this.storage = storage;
        this.recorder = recorder;
    }

    @Override
    public AttachmentView attach(AttachFileCommand command) {
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
