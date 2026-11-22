package io.github.doriangrelu.lifemanager.project.application.port.in.query;

import java.util.UUID;

public record ListProjectsQuery(String requesterId, UUID householdId) {
}
