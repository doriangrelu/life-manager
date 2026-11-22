package io.github.doriangrelu.lifemanager.project.application.port.in.command;

import java.time.LocalDate;
import java.util.UUID;

/** Saisie d'une tâche ; responsable et date facultatifs. */
public record TaskInput(String title, UUID assigneeId, LocalDate dueOn) {
}
