package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/** Taille d'une pièce jointe en octets : de 1 octet à 10 Mo. */
public record FileSize(long bytes) {

    public static final long MAX_BYTES = 10L * 1024 * 1024;

    public FileSize {
        if (bytes < 1) {
            throw new InvalidValueException("Le fichier est vide");
        }
        if (bytes > MAX_BYTES) {
            throw new InvalidValueException("Le fichier ne doit pas dépasser 10 Mo");
        }
    }
}
