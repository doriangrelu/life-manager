package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.Locale;

/** Immatriculation d'un véhicule (« AB-123-CD ») : 12 caractères au plus, toujours en majuscules. */
public record LicensePlate(String value) {

    private static final String FIELD = "L'immatriculation";
    private static final int MAX_LENGTH = 12;

    public LicensePlate {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD).toUpperCase(Locale.ROOT);
    }

    /** L'immatriculation est facultative : un texte absent ou vide donne {@code null}. */
    public static LicensePlate ofNullable(String text) {
        return text == null || text.isBlank() ? null : new LicensePlate(text);
    }
}
