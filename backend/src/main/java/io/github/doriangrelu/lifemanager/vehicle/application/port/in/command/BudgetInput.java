package io.github.doriangrelu.lifemanager.vehicle.application.port.in.command;

import java.util.UUID;

/**
 * Choix « Ajouter au budget » d'une opération.
 *
 * @param categoryId catégorie de dépense du budget
 * @param payerId    membre du foyer qui a payé
 */
public record BudgetInput(UUID categoryId, UUID payerId) {
}
