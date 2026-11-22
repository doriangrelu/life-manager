package io.github.doriangrelu.lifemanager.project.application.port.in.command;

import java.util.List;
import java.util.UUID;

/** Nouvel ordre des tâches d'un projet (identifiants dans l'ordre voulu). */
public record ReorderTasksCommand(String requesterId, UUID householdId, UUID projectId, List<UUID> taskIds) {
}
