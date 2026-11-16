package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.DeleteMaintenanceEntry;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.DeleteMaintenanceEntryCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceEntryRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleAccess;
import io.github.doriangrelu.lifemanager.vehicle.domain.exception.MaintenanceEntryNotFoundException;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Supprime définitivement une opération du carnet d'un véhicule du foyer. */
@Singleton
class DeleteMaintenanceEntryUseCase implements DeleteMaintenanceEntry {

    private final VehicleAccess access;
    private final MaintenanceEntryRepository entries;

    DeleteMaintenanceEntryUseCase(VehicleAccess access, MaintenanceEntryRepository entries) {
        this.access = access;
        this.entries = entries;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public void delete(DeleteMaintenanceEntryCommand command) {
        var vehicle = access.loadForMember(command.householdId(), command.vehicleId(), command.requesterId());
        var entry = entries.findById(new MaintenanceEntryId(command.entryId()))
                .filter(found -> found.belongsTo(vehicle.id()))
                .orElseThrow(MaintenanceEntryNotFoundException::new);
        entries.delete(entry);
    }
}
