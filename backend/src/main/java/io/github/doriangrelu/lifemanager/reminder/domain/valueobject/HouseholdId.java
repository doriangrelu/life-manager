package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'un foyer, tel que publié par le domaine foyer. */
public record HouseholdId(UUID value) {

    private static final String FIELD = "L'identifiant du foyer";

    public HouseholdId {
        Require.notNull(value, FIELD);
    }
}
