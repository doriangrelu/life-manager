package io.github.doriangrelu.lifemanager.project.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'une tâche de projet. */
public record TaskId(UUID value) {

    public TaskId {
        Require.notNull(value, "L'identifiant de la tâche");
    }

    public static TaskId newId() {
        return new TaskId(UUID.randomUUID());
    }
}
