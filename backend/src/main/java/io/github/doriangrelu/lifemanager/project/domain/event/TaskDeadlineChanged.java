package io.github.doriangrelu.lifemanager.project.domain.event;

import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskId;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Date à rappeler pour une tâche, ou plus rien.
 *
 * @param dueOn {@code null} si la tâche n'est plus à rappeler (faite, sans date, supprimée, projet clos)
 */
public record TaskDeadlineChanged(TaskId taskId, HouseholdId householdId, String projectName, String title,
                                  LocalDate dueOn, Instant occurredAt) implements DomainEvent {
}
