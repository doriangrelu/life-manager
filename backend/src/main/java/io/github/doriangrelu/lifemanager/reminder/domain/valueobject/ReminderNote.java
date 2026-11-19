package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Note libre d'un rappel saisi : 280 caractères au plus. */
public record ReminderNote(String value) {

    private static final String FIELD = "La note du rappel";
    private static final int MAX_LENGTH = 280;

    public ReminderNote {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD);
    }

    /** La note est facultative : un texte absent ou vide donne {@code null}. */
    public static ReminderNote ofNullable(String text) {
        return text == null || text.isBlank() ? null : new ReminderNote(text);
    }
}
