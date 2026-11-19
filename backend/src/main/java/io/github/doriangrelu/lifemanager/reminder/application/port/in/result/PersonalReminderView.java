package io.github.doriangrelu.lifemanager.reminder.application.port.in.result;

import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.PersonalReminder;

import java.time.LocalDate;
import java.util.UUID;

/** Un rappel saisi, vu hors du domaine ; {@code note} à {@code null} si absente. */
public record PersonalReminderView(UUID id, String title, String note, LocalDate dueOn, String recurrence,
                                   String notice) {

    public static PersonalReminderView from(PersonalReminder reminder) {
        var details = reminder.details();
        return new PersonalReminderView(reminder.id().value(), details.title().value(),
                details.note() == null ? null : details.note().value(), details.dueOn(),
                details.recurrence().name(), details.notice().name());
    }
}
