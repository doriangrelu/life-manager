package io.github.doriangrelu.lifemanager.vehicle.application.port.out;

import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceExpense;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;

import java.util.Optional;
import java.util.Set;

/** Stockage des opérations envoyées au budget. */
public interface MaintenanceExpenseRepository {

    Optional<MaintenanceExpense> findById(MaintenanceEntryId id);

    /** Opérations du véhicule déjà envoyées au budget. */
    Set<MaintenanceEntryId> findSentOfVehicle(VehicleId vehicleId);

    void save(MaintenanceExpense expense);

    void delete(MaintenanceExpense expense);
}
