package io.github.doriangrelu.lifemanager.shopping.application.port.in.command;

import java.util.UUID;
/**
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 */
public record ClearCheckedItemsCommand(String requesterId, UUID householdId) {
}
