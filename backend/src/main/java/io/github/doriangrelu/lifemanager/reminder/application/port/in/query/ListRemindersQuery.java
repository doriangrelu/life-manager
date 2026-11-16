package io.github.doriangrelu.lifemanager.reminder.application.port.in.query;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 */
public record ListRemindersQuery(String requesterId, UUID householdId) {
}
