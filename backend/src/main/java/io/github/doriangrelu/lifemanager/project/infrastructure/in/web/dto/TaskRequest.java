package io.github.doriangrelu.lifemanager.project.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.project.application.port.in.command.TaskInput;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/** Corps d'ajout ou de modification d'une tâche ; responsable et date facultatifs. */
@Serdeable
public record TaskRequest(@NotBlank @Size(max = 120) String title, @Nullable UUID assigneeId,
                          @Nullable LocalDate dueOn) {

    public TaskInput toInput() {
        return new TaskInput(title, assigneeId, dueOn);
    }
}
