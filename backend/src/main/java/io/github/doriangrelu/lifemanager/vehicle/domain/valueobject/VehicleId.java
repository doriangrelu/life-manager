package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant du véhicule. */
public record VehicleId(UUID value) {

    private static final String FIELD = "L'identifiant du véhicule";

    public VehicleId {
        Require.notNull(value, FIELD);
    }

    public static VehicleId newId() {
        return new VehicleId(UUID.randomUUID());
    }
}
