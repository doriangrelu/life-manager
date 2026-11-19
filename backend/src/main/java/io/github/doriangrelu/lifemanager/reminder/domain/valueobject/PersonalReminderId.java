package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'un rappel saisi par un membre. */
public record PersonalReminderId(UUID value) {

    public PersonalReminderId {
        Require.notNull(value, "L'identifiant du rappel");
    }

    public static PersonalReminderId newId() {
        return new PersonalReminderId(UUID.randomUUID());
    }
}
