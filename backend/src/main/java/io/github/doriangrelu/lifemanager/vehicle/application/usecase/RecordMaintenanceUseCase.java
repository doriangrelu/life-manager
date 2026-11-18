package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.RecordMaintenance;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.RecordMaintenanceCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.MaintenanceEntryView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceEntryRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleAccess;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleInputs;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceEntry;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Ajoute une opération au carnet d'un véhicule du foyer. */
@Singleton
class RecordMaintenanceUseCase implements RecordMaintenance {

    private final VehicleAccess access;
    private final MaintenanceEntryRepository entries;
    private final Clock clock;

    RecordMaintenanceUseCase(VehicleAccess access, MaintenanceEntryRepository entries, Clock clock) {
        this.access = access;
        this.entries = entries;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public MaintenanceEntryView record(RecordMaintenanceCommand command) {
        var details = VehicleInputs.details(command.input());
        var vehicle = access.loadForMember(command.householdId(), command.vehicleId(), command.requesterId());
        var entry = MaintenanceEntry.record(MaintenanceEntryId.newId(), vehicle.id(), details,
                MemberId.of(command.requesterId()), clock);
        entries.save(entry);
        return MaintenanceEntryView.from(entry, 0);
    }
}
