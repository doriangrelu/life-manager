package io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Ligne de la table {@code reminder.reminder_done} (historique des rappels faits). */
@MappedEntity("reminder_done")
public record ReminderDoneEntity(@Id UUID id, UUID householdId, String title, LocalDate dueOn, Instant doneAt,
                                 UUID doneBy) {
}
