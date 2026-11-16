package io.github.doriangrelu.lifemanager.kernel.domain.validation;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/**
 * Vérifications de forme courantes pour les value objects.
 * Équivalent minimal de Guava/Commons {@code Validate}, mais qui lève une {@link InvalidValueException}
 * et garde le domaine sans dépendance externe.
 */
public final class Require {

    private Require() {
    }

    /** Refuse une valeur absente. */
    public static <T> T notNull(T value, String field) {
        if (value == null) {
            throw new InvalidValueException(field + " est obligatoire");
        }
        return value;
    }

    /** Refuse un texte absent ou vide, et le retourne sans espaces autour. */
    public static String notBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException(field + " ne doit pas être vide");
        }
        return value.strip();
    }

    /** Refuse un texte plus long que {@code max} caractères. */
    public static String maxLength(String value, int max, String field) {
        if (value.length() > max) {
            throw new InvalidValueException(field + " ne doit pas dépasser " + max + " caractères");
        }
        return value;
    }
}
