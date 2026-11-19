package io.github.doriangrelu.lifemanager.budget.application.port.out;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.IncomeId;

import java.util.UUID;

/** Correspondance échéance de rentrée récurrente → rentrée : une échéance ne produit jamais deux rentrées. */
public interface ChargeIncomeLinks {

    /**
     * Réserve l'échéance pour cette rentrée.
     *
     * @return faux si l'échéance a déjà sa rentrée (événement reçu une deuxième fois)
     */
    boolean claim(UUID occurrenceId, IncomeId incomeId);
}
