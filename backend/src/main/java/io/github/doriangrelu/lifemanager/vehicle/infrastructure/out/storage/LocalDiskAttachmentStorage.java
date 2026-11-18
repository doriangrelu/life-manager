package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.storage;

import io.github.doriangrelu.lifemanager.kernel.domain.file.StorageKey;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.storage.DiskFileStore;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.AttachmentStorage;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.io.InputStream;
import java.nio.file.Path;

/** Pièces jointes du carnet sur le disque du serveur, dans le dossier {@code vehicle/} (voir {@link DiskFileStore}). */
@Singleton
class LocalDiskAttachmentStorage implements AttachmentStorage {

    private final DiskFileStore files;

    LocalDiskAttachmentStorage(@Value("${life-manager.storage.root}") String storageRoot) {
        this.files = new DiskFileStore(storageRoot, "vehicle");
    }

    @Override
    public void store(StorageKey key, byte[] content) {
        files.store(key, content);
    }

    @Override
    public InputStream open(StorageKey key) {
        return files.open(key);
    }

    @Override
    public void delete(StorageKey key) {
        files.delete(key);
    }

    /** Chemin du fichier (utilisé par les tests). */
    Path pathOf(StorageKey key) {
        return files.pathOf(key);
    }
}
