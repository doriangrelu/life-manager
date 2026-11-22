package io.github.doriangrelu.lifemanager.project.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

import java.util.Arrays;

/** Couleur d'un projet, parmi une liste fixe (la PWA la décline en clair et en sombre). */
public enum ProjectColor {
    INDIGO, TEAL, GREEN, AMBER, ORANGE, RED, PINK, SLATE;

    /** Retrouve la couleur à partir de son nom, refuse un nom inconnu. */
    public static ProjectColor of(String name) {
        return Arrays.stream(values())
                .filter(color -> color.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("Couleur de projet inconnue : " + name));
    }
}
