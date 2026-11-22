package io.github.doriangrelu.lifemanager.vehicle.application.service;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.WearPartCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.WearPartInput;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.WearPartRepository;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.Vehicle;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.WearPart;
import io.github.doriangrelu.lifemanager.vehicle.domain.exception.WearPartNotFoundException;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Mileage;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartId;
import jakarta.inject.Singleton;

/** Cloisonnement des pièces d'usure : visibles des seuls membres du foyer du véhicule. */
@Singleton
public class WearPartAccess {

    private final VehicleAccess vehicles;
    private final WearPartRepository parts;

    WearPartAccess(VehicleAccess vehicles, WearPartRepository parts) {
        this.vehicles = vehicles;
        this.parts = parts;
    }

    /** @throws io.github.doriangrelu.lifemanager.vehicle.domain.exception.VehicleNotFoundException hors du foyer */
    public Vehicle vehicleOf(WearPartCommand command) {
        return vehicles.loadForMember(command.householdId(), command.vehicleId(), command.requesterId());
    }

    /** @throws WearPartNotFoundException si la pièce n'existe pas pour ce véhicule du foyer */
    public WearPart partOf(Vehicle vehicle, WearPartCommand command) {
        return parts.findById(new WearPartId(command.partId()))
                .filter(part -> part.belongsTo(vehicle.id()))
                .orElseThrow(WearPartNotFoundException::new);
    }

    /** Transforme la saisie en pose vérifiée. */
    public static WearPartDetails details(WearPartInput input) {
        return new WearPartDetails(input.label(), input.fittedOn(),
                new Mileage(input.fittedKm() == null ? -1 : input.fittedKm()), input.replaceAfterKm());
    }
}
