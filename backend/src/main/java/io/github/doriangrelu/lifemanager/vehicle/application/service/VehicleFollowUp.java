package io.github.doriangrelu.lifemanager.vehicle.application.service;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceEntryRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.VehicleRepository;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.Vehicle;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Mileage;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.NextDeadline;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.Optional;

/**
 * Met la fiche du véhicule à jour après une saisie (à appeler dans une transaction) : échéance reportée par
 * une opération du carnet, kilométrage actuel relevé s'il est dépassé.
 */
@Singleton
public class VehicleFollowUp {

    private final MaintenanceEntryRepository entries;
    private final VehicleRepository vehicles;
    private final DomainEventPublisher events;
    private final Clock clock;

    VehicleFollowUp(MaintenanceEntryRepository entries, VehicleRepository vehicles, DomainEventPublisher events,
                    Clock clock) {
        this.entries = entries;
        this.vehicles = vehicles;
        this.events = events;
        this.clock = clock;
    }

    /** Un ancien contrôle saisi après coup ne recule pas l'échéance fixée par un plus récent. */
    public void afterMaintenance(Vehicle vehicle, MaintenanceDetails details, Optional<NextDeadline> next) {
        var changed = vehicle.raiseMileage(details.mileage());
        if (next.isPresent() && !entries.existsLaterOfKind(vehicle.id(), next.get().kind(), details.performedOn())) {
            vehicle.postpone(next.get(), clock);
            changed = true;
        }
        if (changed) {
            vehicles.save(vehicle);
            events.publishEventsOf(vehicle);
        }
    }

    /** Relève le kilométrage actuel si {@code reading} le dépasse (pièce d'usure posée…). */
    public void raiseMileage(Vehicle vehicle, Mileage reading) {
        if (vehicle.raiseMileage(reading)) {
            vehicles.save(vehicle);
        }
    }
}
