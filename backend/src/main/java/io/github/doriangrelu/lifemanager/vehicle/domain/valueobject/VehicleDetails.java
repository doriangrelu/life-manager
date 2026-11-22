package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.Optional;

/**
 * Ce que l'utilisateur saisit sur la fiche d'un véhicule, déjà vérifié dans sa forme.
 *
 * @param name         nom du véhicule
 * @param licensePlate immatriculation, ou {@code null} (voir {@link #optionalLicensePlate()})
 * @param deadlines    prochaines échéances
 * @param mileage      kilométrage actuel, ou {@code null}
 * @param tireSize     taille des pneus, ou {@code null}
 */
public record VehicleDetails(VehicleName name, LicensePlate licensePlate, Deadlines deadlines, Mileage mileage,
                             TireSize tireSize) {

    public VehicleDetails {
        Require.notNull(name, "Le nom du véhicule");
        Require.notNull(deadlines, "Les échéances");
    }

    /** Fiche sans kilométrage ni taille de pneus. */
    public VehicleDetails(VehicleName name, LicensePlate licensePlate, Deadlines deadlines) {
        this(name, licensePlate, deadlines, null, null);
    }

    public VehicleDetails withDeadlines(Deadlines newDeadlines) {
        return new VehicleDetails(name, licensePlate, newDeadlines, mileage, tireSize);
    }

    public VehicleDetails withMileage(Mileage newMileage) {
        return new VehicleDetails(name, licensePlate, deadlines, newMileage, tireSize);
    }

    public Optional<LicensePlate> optionalLicensePlate() {
        return Optional.ofNullable(licensePlate);
    }

    public Optional<Mileage> optionalMileage() {
        return Optional.ofNullable(mileage);
    }

    public Optional<TireSize> optionalTireSize() {
        return Optional.ofNullable(tireSize);
    }
}
