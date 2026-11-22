package io.github.doriangrelu.lifemanager.project.contract.event;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Événement public : date à rappeler pour une tâche de projet (pour les rappels).
 *
 * @param dueOn {@code null} si la tâche n'est plus à rappeler
 */
@Serdeable
public record TaskDeadlineChangedV1(UUID taskId, UUID householdId, String projectName, String title,
                                    @Nullable LocalDate dueOn, Instant occurredAt) {
}
