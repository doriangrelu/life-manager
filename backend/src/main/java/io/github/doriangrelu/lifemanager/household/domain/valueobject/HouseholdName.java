package io.github.doriangrelu.lifemanager.household.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Nom d'un foyer (« Maison », « Maison de famille »…) : 1 à 100 caractères. */
public record HouseholdName(String value) {

    private static final String FIELD = "Le nom du foyer";
    private static final int MAX_LENGTH = 100;

    public HouseholdName {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD);
    }
}
