package io.github.doriangrelu.lifemanager.household.application.port.in.result;

import io.github.doriangrelu.lifemanager.household.domain.aggregate.Household;

import java.util.UUID;

/** Un foyer résumé à son identifiant et son nom (listes, sélecteur de foyer). */
public record HouseholdSummary(UUID id, String name) {

    public static HouseholdSummary from(Household household) {
        return new HouseholdSummary(household.id().value(), household.name().value());
    }
}
