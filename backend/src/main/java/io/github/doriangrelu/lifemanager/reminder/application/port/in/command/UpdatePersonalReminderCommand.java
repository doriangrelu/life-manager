package io.github.doriangrelu.lifemanager.reminder.application.port.in.command;

import java.util.UUID;

/** @param requesterId {@code sub} Keycloak du demandeur */
public record UpdatePersonalReminderCommand(String requesterId, UUID householdId, UUID reminderId,
                                            PersonalReminderInput input) {
}
