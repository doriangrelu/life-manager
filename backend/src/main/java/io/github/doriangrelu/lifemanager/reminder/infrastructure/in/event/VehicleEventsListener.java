package io.github.doriangrelu.lifemanager.reminder.infrastructure.in.event;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.ForgetVehicleDeadlines;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.TrackVehicleDeadlines;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.ForgetVehicleDeadlinesCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.TrackVehicleDeadlinesCommand;
import io.github.doriangrelu.lifemanager.vehicle.contract.event.VehicleDeadlinesChangedV1;
import io.github.doriangrelu.lifemanager.vehicle.contract.event.VehicleRemovedV1;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

/** Écoute les événements publics du domaine véhicule : les rappels suivent ainsi ses échéances. */
@Singleton
class VehicleEventsListener {

    private final TrackVehicleDeadlines trackDeadlines;
    private final ForgetVehicleDeadlines forgetDeadlines;

    VehicleEventsListener(TrackVehicleDeadlines trackDeadlines, ForgetVehicleDeadlines forgetDeadlines) {
        this.trackDeadlines = trackDeadlines;
        this.forgetDeadlines = forgetDeadlines;
    }

    @EventListener
    void on(VehicleDeadlinesChangedV1 event) {
        trackDeadlines.track(new TrackVehicleDeadlinesCommand(event.vehicleId(), event.householdId(),
                event.vehicleName(), event.technicalInspectionDue(), event.serviceDue()));
    }

    @EventListener
    void on(VehicleRemovedV1 event) {
        forgetDeadlines.forget(new ForgetVehicleDeadlinesCommand(event.vehicleId()));
    }
}
