package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

/** Nom d'un véhicule (« Clio », « Vélo cargo »…) : 1 à 40 caractères. */
public record VehicleName(String value) {

    private static final String FIELD = "Le nom du véhicule";
    private static final int MAX_LENGTH = 40;

    public VehicleName {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD);
    }
}
