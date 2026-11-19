package io.github.doriangrelu.lifemanager.budget.application.port.in.command;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.CategoryKind;

import java.util.UUID;
/**
 * Action simple sur une catégorie (archiver, réactiver).
 *
 * @param requesterId {@code sub} Keycloak du demandeur
 * @param householdId foyer concerné
 * @param categoryId  catégorie visée
 * @param kind        sens attendu de la catégorie (une catégorie de l'autre sens est « introuvable »)
 */
public record CategoryActionCommand(String requesterId, UUID householdId, UUID categoryId, CategoryKind kind) {
}
