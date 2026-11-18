package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'un foyer (donné par le domaine foyer). */
public record HouseholdId(UUID value) {

    public HouseholdId {
        Require.notNull(value, "L'identifiant du foyer");
    }
}
