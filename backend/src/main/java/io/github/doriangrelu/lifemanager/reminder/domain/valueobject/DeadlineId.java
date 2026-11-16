package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant de l'échéance. */
public record DeadlineId(UUID value) {

    private static final String FIELD = "L'identifiant de l'échéance";

    public DeadlineId {
        Require.notNull(value, FIELD);
    }

    public static DeadlineId newId() {
        return new DeadlineId(UUID.randomUUID());
    }
}
