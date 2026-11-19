package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Précision d'une opération de type « Autre » (« Essuie-glaces ») : 40 caractères au plus. */
public record MaintenanceOtherLabel(String value) {

    private static final String FIELD = "La précision";
    private static final int MAX_LENGTH = 40;

    public MaintenanceOtherLabel {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD);
    }

    /** La précision est facultative : un texte absent ou vide donne {@code null}. */
    public static MaintenanceOtherLabel ofNullable(String text) {
        return text == null || text.isBlank() ? null : new MaintenanceOtherLabel(text);
    }
}
