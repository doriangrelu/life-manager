package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.RemoveVehicle;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.RemoveVehicleCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceAttachmentRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.VehicleRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.AttachmentRemoval;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Supprime définitivement un véhicule du foyer ; la base supprime son carnet, et ses pièces jointes partent avec. */
@Singleton
class RemoveVehicleUseCase implements RemoveVehicle {

    private final VehicleAccess access;
    private final VehicleRepository vehicles;
    private final MaintenanceAttachmentRepository attachments;
    private final AttachmentRemoval removal;
    private final DomainEventPublisher events;
    private final Clock clock;

    RemoveVehicleUseCase(VehicleAccess access, VehicleRepository vehicles, MaintenanceAttachmentRepository attachments,
                         AttachmentRemoval removal, DomainEventPublisher events, Clock clock) {
        this.access = access;
        this.vehicles = vehicles;
        this.attachments = attachments;
        this.removal = removal;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public void remove(RemoveVehicleCommand command) {
        var vehicle = access.loadForMember(command.householdId(), command.vehicleId(), command.requesterId());
        removal.removeAll(attachments.findAllByVehicle(vehicle.id()));
        vehicle.markRemoved(clock);
        vehicles.delete(vehicle);
        events.publishEventsOf(vehicle);
    }
}
