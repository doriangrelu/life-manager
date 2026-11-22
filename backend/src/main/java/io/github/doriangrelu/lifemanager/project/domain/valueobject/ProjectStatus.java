package io.github.doriangrelu.lifemanager.project.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

import java.util.Arrays;

/** État d'un projet : en cours, terminé ou abandonné. */
public enum ProjectStatus {
    ACTIVE, DONE, ABANDONED;

    /** Retrouve l'état à partir de son nom, refuse un nom inconnu. */
    public static ProjectStatus of(String name) {
        return Arrays.stream(values())
                .filter(status -> status.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("État de projet inconnu : " + name));
    }
}
