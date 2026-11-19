package io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.PersonalReminderView;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.UUID;

/** Un rappel saisi par un membre du foyer. */
@Serdeable
public record PersonalReminderResponse(UUID id, String title, @Nullable String note, LocalDate dueOn,
                                       String recurrence, String notice) {

    public static PersonalReminderResponse from(PersonalReminderView view) {
        return new PersonalReminderResponse(view.id(), view.title(), view.note(), view.dueOn(), view.recurrence(),
                view.notice());
    }
}
