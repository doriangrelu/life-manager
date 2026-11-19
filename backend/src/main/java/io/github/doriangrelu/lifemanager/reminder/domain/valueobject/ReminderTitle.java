package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Titre d'un rappel saisi (« Ramonage », « Déclarer les impôts ») : 1 à 60 caractères. */
public record ReminderTitle(String value) {

    private static final String FIELD = "Le titre du rappel";
    private static final int MAX_LENGTH = 60;

    public ReminderTitle {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD);
    }
}
