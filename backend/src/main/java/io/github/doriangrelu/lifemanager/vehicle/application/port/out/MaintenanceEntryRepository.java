package io.github.doriangrelu.lifemanager.vehicle.application.port.out;

import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceEntry;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;

import java.util.List;
import java.util.Optional;
/** Stockage du carnet d'entretien. */
public interface MaintenanceEntryRepository {

    Optional<MaintenanceEntry> findById(MaintenanceEntryId id);

    /** Opérations du véhicule, la plus récente en premier. */
    List<MaintenanceEntry> findAllByVehicle(VehicleId vehicleId);

    void save(MaintenanceEntry entry);

    /** Supprime définitivement l'opération. */
    void delete(MaintenanceEntry entry);
}
