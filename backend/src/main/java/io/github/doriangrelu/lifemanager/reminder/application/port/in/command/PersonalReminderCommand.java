package io.github.doriangrelu.lifemanager.reminder.application.port.in.command;

import java.util.UUID;

/** Action sur un rappel existant (supprimer, « c'est fait ») ; {@code requesterId} = {@code sub} Keycloak. */
public record PersonalReminderCommand(String requesterId, UUID householdId, UUID reminderId) {
}
