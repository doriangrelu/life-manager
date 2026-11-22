package io.github.doriangrelu.lifemanager.reminder.application.port.in.command;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Date à rappeler pour une tâche de projet.
 *
 * @param dueOn {@code null} si la tâche n'est plus à rappeler
 */
public record TrackTaskDeadlineCommand(UUID taskId, UUID householdId, String projectName, String title,
                                       LocalDate dueOn) {
}
