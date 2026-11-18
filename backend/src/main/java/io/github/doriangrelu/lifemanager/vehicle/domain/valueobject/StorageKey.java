package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/**
 * Clé du fichier dans le stockage, choisie par le serveur : jamais le nom d'origine,
 * pour qu'aucun nom saisi ne puisse désigner un autre emplacement.
 */
public record StorageKey(UUID value) {

    public StorageKey {
        Require.notNull(value, "La clé de stockage");
    }

    public static StorageKey newKey() {
        return new StorageKey(UUID.randomUUID());
    }
}
