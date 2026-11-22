package io.github.doriangrelu.lifemanager.project.application.port.in.command;

import java.util.UUID;

public record AddProjectCommand(String requesterId, UUID householdId, ProjectInput input) {
}
