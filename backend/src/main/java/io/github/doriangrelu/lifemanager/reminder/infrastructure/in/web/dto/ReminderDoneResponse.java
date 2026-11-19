package io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.ReminderDoneView;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Une ligne de l'historique des rappels faits ; {@code doneBy} = identifiant du membre. */
@Serdeable
public record ReminderDoneResponse(UUID id, String title, LocalDate dueOn, Instant doneAt, UUID doneBy) {

    public static ReminderDoneResponse from(ReminderDoneView view) {
        return new ReminderDoneResponse(view.id(), view.title(), view.dueOn(), view.doneAt(), view.doneBy());
    }
}
