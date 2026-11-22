package io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.BudgetInput;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Choix « Ajouter au budget » d'une opération.
 *
 * @param categoryId catégorie de dépense du budget
 * @param payerId    membre du foyer qui a payé
 */
@Serdeable
public record BudgetRequest(@NotNull UUID categoryId, @NotNull UUID payerId) {

    public BudgetInput toInput() {
        return new BudgetInput(categoryId, payerId);
    }
}
