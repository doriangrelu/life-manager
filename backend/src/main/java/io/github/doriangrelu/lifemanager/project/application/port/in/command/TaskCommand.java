package io.github.doriangrelu.lifemanager.project.application.port.in.command;

import java.util.UUID;

/** Vise une tâche d'un projet du foyer. */
public record TaskCommand(String requesterId, UUID householdId, UUID projectId, UUID taskId) {
}
