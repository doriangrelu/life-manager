package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.storage;

import io.github.doriangrelu.lifemanager.vehicle.application.port.out.AttachmentStorage;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.StorageKey;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Stocke les pièces jointes sur le disque du serveur : {@code <racine>/vehicle/<2 premiers caractères>/<clé>},
 * sans extension ni nom d'origine. Écriture dans un fichier temporaire puis déplacement atomique :
 * un fichier à moitié écrit n'est jamais visible.
 */
@Singleton
class LocalDiskAttachmentStorage implements AttachmentStorage {

    private final Path root;

    LocalDiskAttachmentStorage(@Value("${life-manager.storage.root}") String storageRoot) {
        this.root = Path.of(storageRoot).toAbsolutePath().normalize().resolve("vehicle");
    }

    @Override
    public void store(StorageKey key, byte[] content) {
        var target = pathOf(key);
        try {
            Files.createDirectories(target.getParent());
            var temporary = Files.createTempFile(target.getParent(), "upload-", ".tmp");
            try {
                Files.write(temporary, content);
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE);
            } finally {
                Files.deleteIfExists(temporary);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Écriture de la pièce jointe impossible", e);
        }
    }

    @Override
    public InputStream open(StorageKey key) {
        try {
            return Files.newInputStream(pathOf(key));
        } catch (IOException e) {
            throw new UncheckedIOException("Lecture de la pièce jointe impossible", e);
        }
    }

    @Override
    public void delete(StorageKey key) {
        try {
            Files.deleteIfExists(pathOf(key));
        } catch (IOException e) {
            throw new UncheckedIOException("Suppression de la pièce jointe impossible", e);
        }
    }

    /** Chemin du fichier ; la clé est un UUID, elle ne peut pas sortir du dossier. */
    Path pathOf(StorageKey key) {
        var name = key.value().toString();
        return root.resolve(name.substring(0, 2)).resolve(name);
    }
}
