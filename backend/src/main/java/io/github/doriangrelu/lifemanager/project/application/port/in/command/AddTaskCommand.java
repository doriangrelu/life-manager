package io.github.doriangrelu.lifemanager.project.application.port.in.command;

import java.util.UUID;

public record AddTaskCommand(String requesterId, UUID householdId, UUID projectId, TaskInput input) {
}
