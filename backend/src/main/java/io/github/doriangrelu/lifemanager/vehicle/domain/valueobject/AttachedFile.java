package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Un fichier joint : son nom d'origine, son type vérifié, sa taille et sa clé dans le stockage. */
public record AttachedFile(AttachmentFileName fileName, AttachmentMediaType mediaType, FileSize size,
                           StorageKey storageKey) {

    public AttachedFile {
        Require.notNull(fileName, "Le nom du fichier");
        Require.notNull(mediaType, "Le type du fichier");
        Require.notNull(size, "La taille du fichier");
        Require.notNull(storageKey, "La clé de stockage");
    }
}
