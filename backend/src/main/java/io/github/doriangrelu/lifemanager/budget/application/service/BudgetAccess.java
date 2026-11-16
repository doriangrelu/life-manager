package io.github.doriangrelu.lifemanager.budget.application.service;

import io.github.doriangrelu.lifemanager.budget.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.budget.domain.exception.BudgetHouseholdNotFoundException;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.budget.domain.valueobject.MemberId;
import jakarta.inject.Singleton;

import java.util.UUID;

/**
 * Cloisonnement du budget : seuls les membres d'un foyer accèdent à ses catégories et dépenses.
 * Tout use case du budget qui cible un foyer passe par ici.
 */
@Singleton
public class BudgetAccess {

    private final HouseholdMembers members;

    BudgetAccess(HouseholdMembers members) {
        this.members = members;
    }

    /**
     * Vérifie que le demandeur est membre du foyer et retourne l'identifiant du foyer.
     *
     * @throws BudgetHouseholdNotFoundException sinon (le foyer reste invisible pour un étranger)
     */
    public HouseholdId householdOfMember(UUID householdId, String requesterId) {
        var household = new HouseholdId(householdId);
        if (!members.isMember(household, MemberId.of(requesterId))) {
            throw new BudgetHouseholdNotFoundException();
        }
        return household;
    }
}
