package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.Arrays;

/** Type de pièce d'usure suivie ; la courroie ne concerne pas les moteurs à chaîne (on ne l'ajoute pas). */
public enum WearPartKind {
    TIRES_FRONT, TIRES_REAR, PADS_FRONT, PADS_REAR, DISCS_FRONT, DISCS_REAR, BATTERY, TIMING_BELT;

    /** Retrouve le type à partir de son nom, refuse un nom inconnu. */
    public static WearPartKind of(String name) {
        var wanted = Require.notBlank(name, "Le type de pièce");
        return Arrays.stream(values())
                .filter(kind -> kind.name().equals(wanted))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("Type de pièce inconnu : " + wanted));
    }
}
