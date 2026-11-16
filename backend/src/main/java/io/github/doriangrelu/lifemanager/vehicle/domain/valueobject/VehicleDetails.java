package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.Optional;

/**
 * Ce que l'utilisateur saisit sur la fiche d'un véhicule, déjà vérifié dans sa forme.
 *
 * @param name         nom du véhicule
 * @param licensePlate immatriculation, ou {@code null} (voir {@link #optionalLicensePlate()})
 * @param deadlines    prochaines échéances
 */
public record VehicleDetails(VehicleName name, LicensePlate licensePlate, Deadlines deadlines) {

    public VehicleDetails {
        Require.notNull(name, "Le nom du véhicule");
        Require.notNull(deadlines, "Les échéances");
    }

    public Optional<LicensePlate> optionalLicensePlate() {
        return Optional.ofNullable(licensePlate);
    }
}
