package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.ListMaintenanceEntries;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.query.ListMaintenanceEntriesQuery;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.MaintenanceEntryView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceAttachmentRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceEntryRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Donne le carnet d'entretien d'un véhicule du foyer, avec le nombre de pièces jointes de chaque opération. */
@Singleton
class ListMaintenanceEntriesUseCase implements ListMaintenanceEntries {

    private final VehicleAccess access;
    private final MaintenanceEntryRepository entries;
    private final MaintenanceAttachmentRepository attachments;

    ListMaintenanceEntriesUseCase(VehicleAccess access, MaintenanceEntryRepository entries,
                                  MaintenanceAttachmentRepository attachments) {
        this.access = access;
        this.entries = entries;
        this.attachments = attachments;
    }

    @Override
    @Transactional(transactionManager = "vehicle", readOnly = true)
    public List<MaintenanceEntryView> list(ListMaintenanceEntriesQuery query) {
        var vehicle = access.loadForMember(query.householdId(), query.vehicleId(), query.requesterId());
        var counts = attachments.countByEntryOfVehicle(vehicle.id());
        return entries.findAllByVehicle(vehicle.id()).stream()
                .map(entry -> MaintenanceEntryView.from(entry, counts.getOrDefault(entry.id(), 0L)))
                .toList();
    }
}
