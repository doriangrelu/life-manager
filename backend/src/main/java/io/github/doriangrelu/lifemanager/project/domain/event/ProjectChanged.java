package io.github.doriangrelu.lifemanager.project.domain.event;

import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectDetails;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectId;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;

import java.time.Instant;

/** Un projet a été créé ou modifié. */
public record ProjectChanged(ProjectId projectId, HouseholdId householdId, ProjectDetails details,
                             Instant occurredAt) implements DomainEvent {
}
