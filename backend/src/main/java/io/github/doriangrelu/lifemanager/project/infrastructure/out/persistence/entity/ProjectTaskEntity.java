package io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.entity;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Ligne de la table {@code project.project_task}. */
@MappedEntity("project_task")
public record ProjectTaskEntity(@Id UUID id, UUID projectId, UUID householdId, String title,
                                @Nullable UUID assigneeId, @Nullable LocalDate dueOn, @Nullable Instant doneAt,
                                int position) {
}
