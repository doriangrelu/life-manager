package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.ListWearParts;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.WearPartCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.WearPartView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.WearPartRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.WearPartAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Pièces d'usure d'un véhicule du foyer, avec les km parcourus (calculés avec le kilométrage actuel). */
@Singleton
class ListWearPartsUseCase implements ListWearParts {

    private final WearPartAccess access;
    private final WearPartRepository parts;

    ListWearPartsUseCase(WearPartAccess access, WearPartRepository parts) {
        this.access = access;
        this.parts = parts;
    }

    @Override
    @Transactional(transactionManager = "vehicle", readOnly = true)
    public List<WearPartView> list(WearPartCommand command) {
        var vehicle = access.vehicleOf(command);
        var current = vehicle.details().mileage();
        return parts.findAllByVehicle(vehicle.id()).stream().map(part -> WearPartView.from(part, current)).toList();
    }
}
