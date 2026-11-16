package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.UpdateVehicle;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.UpdateVehicleCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.VehicleView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.VehicleRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleAccess;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleInputs;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Remplace la fiche d'un véhicule du foyer (nom, immatriculation, échéances). */
@Singleton
class UpdateVehicleUseCase implements UpdateVehicle {

    private final VehicleAccess access;
    private final VehicleRepository vehicles;
    private final DomainEventPublisher events;
    private final Clock clock;

    UpdateVehicleUseCase(VehicleAccess access, VehicleRepository vehicles, DomainEventPublisher events,
                         Clock clock) {
        this.access = access;
        this.vehicles = vehicles;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public VehicleView update(UpdateVehicleCommand command) {
        var details = VehicleInputs.details(command.input());
        var vehicle = access.loadForMember(command.householdId(), command.vehicleId(), command.requesterId());
        vehicle.update(details, clock);
        vehicles.save(vehicle);
        events.publishEventsOf(vehicle);
        return VehicleView.from(vehicle);
    }
}
