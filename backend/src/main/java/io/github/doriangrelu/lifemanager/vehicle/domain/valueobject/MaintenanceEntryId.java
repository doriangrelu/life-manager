package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant de l'opération d'entretien. */
public record MaintenanceEntryId(UUID value) {

    private static final String FIELD = "L'identifiant de l'opération d'entretien";

    public MaintenanceEntryId {
        Require.notNull(value, FIELD);
    }

    public static MaintenanceEntryId newId() {
        return new MaintenanceEntryId(UUID.randomUUID());
    }
}
