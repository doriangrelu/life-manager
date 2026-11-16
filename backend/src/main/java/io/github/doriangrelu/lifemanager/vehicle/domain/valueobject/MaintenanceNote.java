package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Note libre sur une opération d'entretien (« Garage Martin, plaquettes AV ») : 140 caractères au plus. */
public record MaintenanceNote(String value) {

    private static final String FIELD = "La note";
    private static final int MAX_LENGTH = 140;

    public MaintenanceNote {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD);
    }

    /** La note est facultative : un texte absent ou vide donne {@code null}. */
    public static MaintenanceNote ofNullable(String text) {
        return text == null || text.isBlank() ? null : new MaintenanceNote(text);
    }
}
