package io.github.doriangrelu.lifemanager.reminder.application.port.in.command;

import java.util.UUID;

/** Retrait d'une ligne d'historique ; {@code requesterId} = {@code sub} Keycloak. */
public record RemoveReminderDoneCommand(String requesterId, UUID householdId, UUID entryId) {
}
