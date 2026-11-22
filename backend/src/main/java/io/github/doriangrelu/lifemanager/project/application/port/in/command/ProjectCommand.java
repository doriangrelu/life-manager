package io.github.doriangrelu.lifemanager.project.application.port.in.command;

import java.util.UUID;

/** Vise un projet du foyer (lecture, suppression). */
public record ProjectCommand(String requesterId, UUID householdId, UUID projectId) {
}
