package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'une pièce d'usure. */
public record WearPartId(UUID value) {

    public WearPartId {
        Require.notNull(value, "L'identifiant de la pièce");
    }

    public static WearPartId newId() {
        return new WearPartId(UUID.randomUUID());
    }
}
