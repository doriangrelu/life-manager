package io.github.doriangrelu.lifemanager.project.domain.valueobject;

import java.time.Instant;

/**
 * Avancement et place d'une tâche dans sa liste.
 *
 * @param doneAt   moment où elle a été cochée, ou {@code null}
 * @param position rang dans la liste du projet (0 en haut)
 */
public record TaskState(Instant doneAt, int position) {
}
