package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/**
 * Rappel donné par une échéance à une date donnée.
 *
 * @param status   en retard ou bientôt
 * @param daysLeft jours restants avant l'échéance, négatif en cas de retard
 */
public record Reminder(ReminderStatus status, long daysLeft) {

    public Reminder {
        Require.notNull(status, "Le statut du rappel");
    }
}
