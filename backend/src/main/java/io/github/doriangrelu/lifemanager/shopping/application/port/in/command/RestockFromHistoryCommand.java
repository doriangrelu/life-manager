package io.github.doriangrelu.lifemanager.shopping.application.port.in.command;

import java.util.List;
import java.util.UUID;

/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param listId      sous-liste visée
 * @param entryIds    éléments de l'historique à remettre en liste
 */
public record RestockFromHistoryCommand(String requesterId, UUID householdId, UUID listId, List<UUID> entryIds) {

    public RestockFromHistoryCommand {
        entryIds = List.copyOf(entryIds);
    }
}
