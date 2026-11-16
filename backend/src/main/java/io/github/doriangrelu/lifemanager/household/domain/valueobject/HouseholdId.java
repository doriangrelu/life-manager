package io.github.doriangrelu.lifemanager.household.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'un foyer. */
public record HouseholdId(UUID value) {

    public HouseholdId {
        Require.notNull(value, "L'identifiant du foyer");
    }

    public static HouseholdId newId() {
        return new HouseholdId(UUID.randomUUID());
    }
}
