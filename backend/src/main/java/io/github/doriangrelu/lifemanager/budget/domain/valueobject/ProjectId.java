package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.UUID;

/** Identifiant d'un projet du foyer (le projet lui-même vit dans le domaine project). */
public record ProjectId(UUID value) {

    public ProjectId {
        Require.notNull(value, "L'identifiant du projet");
    }

    public static ProjectId ofNullable(UUID value) {
        return value == null ? null : new ProjectId(value);
    }
}
