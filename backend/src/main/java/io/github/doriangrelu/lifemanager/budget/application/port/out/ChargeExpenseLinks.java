package io.github.doriangrelu.lifemanager.budget.application.port.out;

import io.github.doriangrelu.lifemanager.budget.domain.valueobject.ExpenseId;

import java.util.UUID;

/** Correspondance échéance de charge → dépense : une échéance ne produit jamais deux dépenses. */
public interface ChargeExpenseLinks {

    /**
     * Réserve l'échéance pour cette dépense.
     *
     * @return faux si l'échéance a déjà sa dépense (événement reçu une deuxième fois)
     */
    boolean claim(UUID occurrenceId, ExpenseId expenseId);
}
