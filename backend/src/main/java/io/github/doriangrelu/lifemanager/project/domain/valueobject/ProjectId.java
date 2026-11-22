package io.github.doriangrelu.lifemanager.project.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'un projet du foyer. */
public record ProjectId(UUID value) {

    public ProjectId {
        Require.notNull(value, "L'identifiant du projet");
    }

    public static ProjectId newId() {
        return new ProjectId(UUID.randomUUID());
    }
}
