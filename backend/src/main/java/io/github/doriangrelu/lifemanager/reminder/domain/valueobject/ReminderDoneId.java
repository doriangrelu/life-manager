package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'une ligne de l'historique des rappels faits. */
public record ReminderDoneId(UUID value) {

    public ReminderDoneId {
        Require.notNull(value, "L'identifiant de la ligne d'historique");
    }

    public static ReminderDoneId newId() {
        return new ReminderDoneId(UUID.randomUUID());
    }
}
