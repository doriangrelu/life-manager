package io.github.doriangrelu.lifemanager.reminder.application.port.in.command;

import java.time.LocalDate;

/**
 * Saisie d'un rappel, telle que reçue (vérifiée par le domaine).
 *
 * @param recurrence NONE, MONTHLY, QUARTERLY ou YEARLY
 * @param notice     SAME_DAY, ONE_WEEK ou ONE_MONTH
 */
public record PersonalReminderInput(String title, String note, LocalDate dueOn, String recurrence, String notice) {
}
