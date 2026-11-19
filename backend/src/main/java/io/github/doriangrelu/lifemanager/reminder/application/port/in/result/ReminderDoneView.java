package io.github.doriangrelu.lifemanager.reminder.application.port.in.result;

import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.ReminderDone;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Une ligne de l'historique, vue hors du domaine ; {@code doneBy} = identifiant du membre. */
public record ReminderDoneView(UUID id, String title, LocalDate dueOn, Instant doneAt, UUID doneBy) {

    public static ReminderDoneView from(ReminderDone entry) {
        var completion = entry.completion();
        return new ReminderDoneView(entry.id().value(), completion.title().value(), completion.dueOn(),
                completion.doneAt(), completion.doneBy().value());
    }
}
