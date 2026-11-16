package io.github.doriangrelu.lifemanager.vehicle.application.port.out;

import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.Vehicle;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;

import java.util.List;
import java.util.Optional;
/** Stockage des véhicules. */
public interface VehicleRepository {

    Optional<Vehicle> findById(VehicleId id);

    /** Véhicules du foyer, triés par nom. */
    List<Vehicle> findAllByHousehold(HouseholdId householdId);

    /** Crée le véhicule ou enregistre ses modifications. */
    void save(Vehicle vehicle);

    /** Supprime définitivement le véhicule et son carnet. */
    void delete(Vehicle vehicle);
}
