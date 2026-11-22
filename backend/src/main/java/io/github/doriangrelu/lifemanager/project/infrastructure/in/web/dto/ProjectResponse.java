package io.github.doriangrelu.lifemanager.project.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.project.application.port.in.result.ProjectView;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.UUID;

/** Un projet du foyer, avec l'avancement de ses tâches. */
@Serdeable
public record ProjectResponse(UUID id, String name, @Nullable String description, String color,
                              @Nullable Long plannedCents, @Nullable LocalDate startOn, @Nullable LocalDate targetOn,
                              String status, boolean countsInBudget, int tasksDone, int tasksTotal) {

    public static ProjectResponse from(ProjectView v) {
        return new ProjectResponse(v.id(), v.name(), v.description(), v.color(), v.plannedCents(), v.startOn(),
                v.targetOn(), v.status(), v.countsInBudget(), v.tasksDone(), v.tasksTotal());
    }
}
