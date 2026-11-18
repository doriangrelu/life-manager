package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant de la charge. */
public record ChargeId(UUID value) {

    public ChargeId {
        Require.notNull(value, "L'identifiant de la charge");
    }

    public static ChargeId newId() {
        return new ChargeId(UUID.randomUUID());
    }
}
