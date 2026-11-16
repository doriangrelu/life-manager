package io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.entity;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.LocalDate;
import java.util.UUID;

/** Ligne de la table {@code reminder.deadline}. */
@MappedEntity("deadline")
public record DeadlineEntity(@Id UUID id, UUID householdId, UUID sourceId, String kind, String subject,
                             LocalDate dueOn) {
}
