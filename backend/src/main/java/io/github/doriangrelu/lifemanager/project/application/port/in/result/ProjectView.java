package io.github.doriangrelu.lifemanager.project.application.port.in.result;

import io.github.doriangrelu.lifemanager.project.domain.aggregate.Project;

import java.time.LocalDate;
import java.util.UUID;

/** Un projet tel que vu hors du domaine, avec l'avancement de ses tâches. */
public record ProjectView(UUID id, String name, String description, String color, Long plannedCents,
                          LocalDate startOn, LocalDate targetOn, String status, boolean countsInBudget,
                          int tasksDone, int tasksTotal) {

    public static ProjectView of(Project project, int tasksDone, int tasksTotal) {
        var d = project.details();
        return new ProjectView(project.id().value(), d.name().value(), d.description(), d.color().name(),
                d.plannedCents(), d.startOn(), d.targetOn(), d.status().name(), d.countsInBudget(), tasksDone,
                tasksTotal);
    }
}
