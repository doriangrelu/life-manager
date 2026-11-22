package io.github.doriangrelu.lifemanager.project.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;

/**
 * Ce qui décrit une tâche.
 *
 * @param assignee membre qui s'en charge, ou {@code null}
 * @param dueOn    date visée, ou {@code null}
 */
public record TaskDetails(TaskTitle title, MemberId assignee, LocalDate dueOn) {

    public TaskDetails {
        Require.notNull(title, "Le titre de la tâche");
    }
}
