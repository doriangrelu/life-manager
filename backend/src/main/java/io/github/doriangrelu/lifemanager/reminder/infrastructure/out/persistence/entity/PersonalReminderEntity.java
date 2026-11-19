package io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.LocalDate;
import java.util.UUID;

/** Ligne de la table {@code reminder.personal_reminder} ; {@code anchorOn} = date de départ de la récurrence. */
@MappedEntity("personal_reminder")
public record PersonalReminderEntity(@Id UUID id, UUID householdId, String title, @Nullable String note,
                                     LocalDate dueOn, LocalDate anchorOn, String recurrence, String notice) {
}
