package io.github.doriangrelu.lifemanager.project.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.project.application.port.in.result.TaskView;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.UUID;

/** Une tâche de projet. */
@Serdeable
public record TaskResponse(UUID id, String title, @Nullable UUID assigneeId, @Nullable LocalDate dueOn,
                           boolean done) {

    public static TaskResponse from(TaskView v) {
        return new TaskResponse(v.id(), v.title(), v.assigneeId(), v.dueOn(), v.done());
    }
}
