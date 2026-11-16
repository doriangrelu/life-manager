package io.github.doriangrelu.lifemanager.budget.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.NotFoundException;

/** Foyer inconnu du budget, ou dont l'utilisateur n'est pas membre (même erreur dans les deux cas). */
public class BudgetHouseholdNotFoundException extends NotFoundException {

    public BudgetHouseholdNotFoundException() {
        super("Foyer introuvable");
    }
}
