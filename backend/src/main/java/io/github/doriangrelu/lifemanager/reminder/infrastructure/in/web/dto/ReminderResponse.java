package io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.ReminderView;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Un rappel d'échéance.
 *
 * @param kind     TECHNICAL_INSPECTION, SERVICE, CHARGE, CHARGE_TO_CONFIRM, ou PERSONAL (rappel saisi)
 * @param status   OVERDUE (en retard) ou DUE_SOON (dans les 30 jours)
 * @param daysLeft jours restants, négatif en cas de retard
 */
@Serdeable
public record ReminderResponse(UUID deadlineId, String kind, String subject, LocalDate dueOn, String status,
                               long daysLeft) {

    public static ReminderResponse from(ReminderView reminder) {
        return new ReminderResponse(reminder.deadlineId(), reminder.kind(), reminder.subject(), reminder.dueOn(),
                reminder.status(), reminder.daysLeft());
    }
}
