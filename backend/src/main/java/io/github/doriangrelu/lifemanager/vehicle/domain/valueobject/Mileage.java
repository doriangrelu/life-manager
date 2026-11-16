package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/** Kilométrage relevé au compteur : 0 à 2 000 000 km. */
public record Mileage(int kilometers) {

    private static final int MAX = 2_000_000;

    public Mileage {
        if (kilometers < 0 || kilometers > MAX) {
            throw new InvalidValueException("Le kilométrage doit être compris entre 0 et 2 000 000 km");
        }
    }

    /** Le kilométrage est facultatif : {@code null} donne {@code null}. */
    public static Mileage ofNullable(Integer kilometers) {
        return kilometers == null ? null : new Mileage(kilometers);
    }
}
