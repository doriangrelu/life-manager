package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.Locale;

/** Taille des pneus (« 205/55 R16 91V ») : 30 caractères au plus, espaces réduits, en majuscules. */
public record TireSize(String value) {

    private static final String FIELD = "La taille des pneus";
    private static final int MAX_LENGTH = 30;

    public TireSize {
        value = Require.maxLength(Require.notBlank(value, FIELD).trim().replaceAll("\\s+", " "), MAX_LENGTH, FIELD)
                .toUpperCase(Locale.ROOT);
    }

    /** Facultative : un texte absent ou vide donne {@code null}. */
    public static TireSize ofNullable(String text) {
        return text == null || text.isBlank() ? null : new TireSize(text);
    }
}
