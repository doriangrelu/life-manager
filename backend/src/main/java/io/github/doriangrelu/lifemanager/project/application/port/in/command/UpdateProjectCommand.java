package io.github.doriangrelu.lifemanager.project.application.port.in.command;

import java.util.UUID;

public record UpdateProjectCommand(String requesterId, UUID householdId, UUID projectId, ProjectInput input) {
}
