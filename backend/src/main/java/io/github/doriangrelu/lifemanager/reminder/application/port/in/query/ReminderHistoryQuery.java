package io.github.doriangrelu.lifemanager.reminder.application.port.in.query;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

import java.util.UUID;

/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param offset      nombre de lignes déjà affichées (0 pour la première page)
 */
public record ReminderHistoryQuery(String requesterId, UUID householdId, int offset) {

    public ReminderHistoryQuery {
        if (offset < 0) {
            throw new InvalidValueException("Le décalage de l'historique ne peut pas être négatif");
        }
    }
}
