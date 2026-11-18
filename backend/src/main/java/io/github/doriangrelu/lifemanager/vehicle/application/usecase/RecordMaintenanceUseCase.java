package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.RecordMaintenance;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.RecordMaintenanceCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.MaintenanceEntryView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceEntryRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.VehicleRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleAccess;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleInputs;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceEntry;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.Vehicle;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.NextDeadline;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Ajoute une opération au carnet d'un véhicule du foyer. Un contrôle technique ou une révision reporte aussi
 * l'échéance du véhicule, dans la même transaction, sauf si le carnet en contient déjà un plus récent.
 */
@Singleton
class RecordMaintenanceUseCase implements RecordMaintenance {

    private final VehicleAccess access;
    private final MaintenanceEntryRepository entries;
    private final VehicleRepository vehicles;
    private final DomainEventPublisher events;
    private final Clock clock;

    RecordMaintenanceUseCase(VehicleAccess access, MaintenanceEntryRepository entries, VehicleRepository vehicles,
                             DomainEventPublisher events, Clock clock) {
        this.access = access;
        this.entries = entries;
        this.vehicles = vehicles;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public MaintenanceEntryView record(RecordMaintenanceCommand command) {
        var details = VehicleInputs.details(command.input());
        // Vérifiée avant tout enregistrement : une date précise invalide ne laisse rien en base.
        var next = NextDeadline.after(details, command.input().nextDueOn());
        var vehicle = access.loadForMember(command.householdId(), command.vehicleId(), command.requesterId());
        var entry = MaintenanceEntry.record(MaintenanceEntryId.newId(), vehicle.id(), details,
                MemberId.of(command.requesterId()), clock);
        entries.save(entry);
        next.ifPresent(deadline -> postponeUnlessLater(vehicle, deadline, details.performedOn()));
        return MaintenanceEntryView.from(entry, 0);
    }

    /** Un ancien contrôle saisi après coup ne recule pas l'échéance fixée par un plus récent. */
    private void postponeUnlessLater(Vehicle vehicle, NextDeadline deadline, LocalDate performedOn) {
        if (entries.existsLaterOfKind(vehicle.id(), deadline.kind(), performedOn)) {
            return;
        }
        vehicle.postpone(deadline, clock);
        vehicles.save(vehicle);
        events.publishEventsOf(vehicle);
    }
}
