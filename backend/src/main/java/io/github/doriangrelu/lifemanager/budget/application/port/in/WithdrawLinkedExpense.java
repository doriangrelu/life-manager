package io.github.doriangrelu.lifemanager.budget.application.port.in;

import java.util.UUID;

/** Supprime la dépense produite par une opération d'entretien supprimée (événement du domaine véhicule). */
public interface WithdrawLinkedExpense {

    void withdraw(UUID sourceId, UUID householdId);
}
