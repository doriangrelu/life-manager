package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Ce que concerne une échéance, tel qu'affiché à l'utilisateur (nom du véhicule ou de la charge) : 1 à 60 caractères. */
public record Subject(String value) {

    private static final String FIELD = "Le sujet de l'échéance";
    private static final int MAX_LENGTH = 60;

    public Subject {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD);
    }
}
