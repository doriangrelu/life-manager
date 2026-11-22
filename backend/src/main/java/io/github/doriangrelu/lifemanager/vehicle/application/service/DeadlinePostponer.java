package io.github.doriangrelu.lifemanager.vehicle.application.service;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceEntryRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.VehicleRepository;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.Vehicle;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.NextDeadline;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.LocalDate;

/** Reporte l'échéance d'un véhicule après une opération du carnet (à appeler dans une transaction). */
@Singleton
public class DeadlinePostponer {

    private final MaintenanceEntryRepository entries;
    private final VehicleRepository vehicles;
    private final DomainEventPublisher events;
    private final Clock clock;

    DeadlinePostponer(MaintenanceEntryRepository entries, VehicleRepository vehicles, DomainEventPublisher events,
                      Clock clock) {
        this.entries = entries;
        this.vehicles = vehicles;
        this.events = events;
        this.clock = clock;
    }

    /** Un ancien contrôle saisi après coup ne recule pas l'échéance fixée par un plus récent. */
    public void postponeUnlessLater(Vehicle vehicle, NextDeadline deadline, LocalDate performedOn) {
        if (entries.existsLaterOfKind(vehicle.id(), deadline.kind(), performedOn)) {
            return;
        }
        vehicle.postpone(deadline, clock);
        vehicles.save(vehicle);
        events.publishEventsOf(vehicle);
    }
}
