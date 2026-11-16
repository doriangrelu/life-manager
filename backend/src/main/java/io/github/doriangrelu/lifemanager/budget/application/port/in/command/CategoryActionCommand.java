package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import java.util.UUID;
/**
 * Action simple sur une catégorie (archiver, réactiver).
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param categoryId  catégorie visée
 */
public record CategoryActionCommand(String requesterId, UUID householdId, UUID categoryId) {
}
