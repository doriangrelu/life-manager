package io.github.doriangrelu.lifemanager.project.domain.event;

import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectId;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;

import java.time.Instant;

/** Un projet a été supprimé, avec ses tâches. */
public record ProjectDeleted(ProjectId projectId, HouseholdId householdId, Instant occurredAt) implements DomainEvent {
}
