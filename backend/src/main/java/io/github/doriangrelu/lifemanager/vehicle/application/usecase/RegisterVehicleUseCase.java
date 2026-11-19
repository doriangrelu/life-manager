package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.Authorship;
import jakarta.inject.Named;
import java.util.UUID;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.RegisterVehicle;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.RegisterVehicleCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.VehicleView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.VehicleRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleAccess;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleInputs;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.Vehicle;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Ajoute un véhicule au foyer d'un membre. */
@Singleton
class RegisterVehicleUseCase implements RegisterVehicle {

    private final VehicleAccess access;
    private final VehicleRepository vehicles;
    private final DomainEventPublisher events;
    private final Clock clock;
    private final Authorship authorship;

    RegisterVehicleUseCase(VehicleAccess access, VehicleRepository vehicles, DomainEventPublisher events,
                           Clock clock, @Named("vehicle") Authorship authorship) {
        this.authorship = authorship;
        this.access = access;
        this.vehicles = vehicles;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public VehicleView register(RegisterVehicleCommand command) {
        var household = access.householdOfMember(command.householdId(), command.requesterId());
        var vehicle = Vehicle.register(VehicleId.newId(), household, VehicleInputs.details(command.input()), clock);
        vehicles.save(vehicle);
        authorship.record(vehicle.id().value(), household.value(), UUID.fromString(command.requesterId()));
        events.publishEventsOf(vehicle);
        return VehicleView.from(vehicle);
    }
}
