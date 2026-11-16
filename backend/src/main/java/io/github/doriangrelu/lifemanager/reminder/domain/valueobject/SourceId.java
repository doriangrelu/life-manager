package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant de l'objet à l'origine de l'échéance. */
public record SourceId(UUID value) {

    private static final String FIELD = "L'identifiant de l'objet à l'origine de l'échéance";

    public SourceId {
        Require.notNull(value, FIELD);
    }

    public static SourceId newId() {
        return new SourceId(UUID.randomUUID());
    }
}
