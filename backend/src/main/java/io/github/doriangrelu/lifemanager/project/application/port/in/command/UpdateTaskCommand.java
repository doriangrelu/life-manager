package io.github.doriangrelu.lifemanager.project.application.port.in.command;

import java.util.UUID;

public record UpdateTaskCommand(String requesterId, UUID householdId, UUID projectId, UUID taskId, TaskInput input) {
}
