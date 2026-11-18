package io.github.doriangrelu.lifemanager.vehicle.application.port.out;

import io.github.doriangrelu.lifemanager.kernel.domain.file.StorageKey;

import java.io.InputStream;

/** Stockage du contenu des pièces jointes (hors base de données). */
public interface AttachmentStorage {

    /** Enregistre le contenu ; il n'est visible qu'une fois entièrement écrit. */
    void store(StorageKey key, byte[] content);

    /** Ouvre le contenu en lecture ; l'appelant ferme le flux. */
    InputStream open(StorageKey key);

    /** Supprime le contenu ; sans effet s'il n'existe pas. */
    void delete(StorageKey key);
}
