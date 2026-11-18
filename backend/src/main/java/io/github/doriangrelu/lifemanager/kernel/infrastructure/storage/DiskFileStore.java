package io.github.doriangrelu.lifemanager.kernel.infrastructure.storage;

import io.github.doriangrelu.lifemanager.kernel.domain.file.StorageKey;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Fichiers envoyés d'un domaine, sur le disque du serveur : {@code <racine>/<domaine>/<2 premiers caractères>/<clé>},
 * sans extension ni nom d'origine. Écriture dans un fichier temporaire puis déplacement atomique :
 * un fichier à moitié écrit n'est jamais visible. Chaque domaine a son propre dossier (découper = le déplacer).
 */
public final class DiskFileStore {

    private final Path root;

    public DiskFileStore(String storageRoot, String domainFolder) {
        this.root = Path.of(storageRoot).toAbsolutePath().normalize().resolve(domainFolder);
    }

    /** Enregistre le contenu ; il n'est visible qu'une fois entièrement écrit. */
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
            throw new UncheckedIOException("Écriture du fichier impossible", e);
        }
    }

    /** Ouvre le contenu en lecture ; l'appelant ferme le flux. */
    public InputStream open(StorageKey key) {
        try {
            return Files.newInputStream(pathOf(key));
        } catch (IOException e) {
            throw new UncheckedIOException("Lecture du fichier impossible", e);
        }
    }

    /** Supprime le contenu ; sans effet s'il n'existe pas. */
    public void delete(StorageKey key) {
        try {
            Files.deleteIfExists(pathOf(key));
        } catch (IOException e) {
            throw new UncheckedIOException("Suppression du fichier impossible", e);
        }
    }

    /** Chemin du fichier ; la clé est un UUID, elle ne peut pas sortir du dossier. */
    public Path pathOf(StorageKey key) {
        var name = key.value().toString();
        return root.resolve(name.substring(0, 2)).resolve(name);
    }
}
