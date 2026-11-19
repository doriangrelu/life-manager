package io.github.doriangrelu.lifemanager.reminder.application.port.in.result;

import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.Deadline;
import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.PersonalReminder;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Reminder;

import java.time.LocalDate;
import java.util.UUID;
/**
 * Un rappel tel que vu hors du domaine.
 *
 * @param deadlineId identifiant de l'échéance, ou du rappel saisi pour {@code PERSONAL}
 * @param kind     TECHNICAL_INSPECTION, SERVICE, CHARGE, CHARGE_TO_CONFIRM, ou PERSONAL (rappel saisi)
 * @param status   OVERDUE ou DUE_SOON
 * @param daysLeft jours restants, négatif en cas de retard
 */
public record ReminderView(UUID deadlineId, String kind, String subject, LocalDate dueOn, String status,
                           long daysLeft) {

    public static ReminderView of(Deadline deadline, Reminder reminder) {
        var details = deadline.details();
        return new ReminderView(deadline.id().value(), details.kind().name(), details.subject().value(),
                details.dueOn(), reminder.status().name(), reminder.daysLeft());
    }

    /** Un rappel saisi par un membre ; son sujet est son titre. */
    public static ReminderView of(PersonalReminder personal, Reminder reminder) {
        var details = personal.details();
        return new ReminderView(personal.id().value(), "PERSONAL", details.title().value(), details.dueOn(),
                reminder.status().name(), reminder.daysLeft());
    }
}
