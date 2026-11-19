package io.github.doriangrelu.lifemanager.reminder.application.port.in.command;

import java.util.UUID;

/** @param requesterId {@code sub} Keycloak du demandeur */
public record AddPersonalReminderCommand(String requesterId, UUID householdId, PersonalReminderInput input) {
}
