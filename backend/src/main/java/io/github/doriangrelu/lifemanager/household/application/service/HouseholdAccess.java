package io.github.doriangrelu.lifemanager.household.application.service;

import io.github.doriangrelu.lifemanager.household.application.port.out.HouseholdRepository;
import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;
import io.github.doriangrelu.lifemanager.household.domain.exception.HouseholdNotFoundException;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import jakarta.inject.Singleton;

/**
 * Cloisonnement des foyers : seul un membre peut accéder à un foyer.
 * Tout use case qui cible un foyer passe par ici.
 */
@Singleton
public class HouseholdAccess {

    private final HouseholdRepository households;

    HouseholdAccess(HouseholdRepository households) {
        this.households = households;
    }

    /**
     * Charge le foyer si {@code member} en fait partie.
     *
     * @throws HouseholdNotFoundException si le foyer n'existe pas ou si l'utilisateur n'en est pas membre
     */
    public Household loadForMember(HouseholdId householdId, MemberId member) {
        return households.findById(householdId)
                .filter(household -> household.isMember(member))
                .orElseThrow(HouseholdNotFoundException::new);
    }
}
