package io.github.doriangrelu.lifemanager.project.application.port.in.result;

import io.github.doriangrelu.lifemanager.project.domain.aggregate.ProjectTask;

import java.time.LocalDate;
import java.util.UUID;

/** Une tâche telle que vue hors du domaine. */
public record TaskView(UUID id, String title, UUID assigneeId, LocalDate dueOn, boolean done) {

    public static TaskView of(ProjectTask task) {
        var d = task.details();
        return new TaskView(task.id().value(), d.title().value(), d.assignee() == null ? null : d.assignee().value(),
                d.dueOn(), task.isDone());
    }
}
