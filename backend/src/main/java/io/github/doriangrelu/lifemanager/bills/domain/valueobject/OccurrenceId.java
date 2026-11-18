package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant de l'échéance. */
public record OccurrenceId(UUID value) {

    public OccurrenceId {
        Require.notNull(value, "L'identifiant de l'échéance");
    }

    public static OccurrenceId newId() {
        return new OccurrenceId(UUID.randomUUID());
    }
}
