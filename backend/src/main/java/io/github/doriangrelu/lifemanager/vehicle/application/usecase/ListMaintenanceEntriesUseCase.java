package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.ListMaintenanceEntries;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.query.ListMaintenanceEntriesQuery;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.MaintenanceEntryView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceEntryRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;
/** Donne le carnet d'entretien d'un véhicule du foyer. */
@Singleton
class ListMaintenanceEntriesUseCase implements ListMaintenanceEntries {

    private final VehicleAccess access;
    private final MaintenanceEntryRepository entries;

    ListMaintenanceEntriesUseCase(VehicleAccess access, MaintenanceEntryRepository entries) {
        this.access = access;
        this.entries = entries;
    }

    @Override
    @Transactional(transactionManager = "vehicle", readOnly = true)
    public List<MaintenanceEntryView> list(ListMaintenanceEntriesQuery query) {
        var vehicle = access.loadForMember(query.householdId(), query.vehicleId(), query.requesterId());
        return entries.findAllByVehicle(vehicle.id()).stream().map(MaintenanceEntryView::from).toList();
    }
}
