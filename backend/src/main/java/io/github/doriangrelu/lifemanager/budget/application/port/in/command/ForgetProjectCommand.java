package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import java.util.UUID;

public record ForgetProjectCommand(UUID projectId) {
}
