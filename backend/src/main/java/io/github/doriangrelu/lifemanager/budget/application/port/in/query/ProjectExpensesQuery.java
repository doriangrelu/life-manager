package io.github.doriangrelu.lifemanager.budget.application.port.in.query;

import java.util.UUID;

public record ProjectExpensesQuery(String requesterId, UUID householdId, UUID projectId) {
}
