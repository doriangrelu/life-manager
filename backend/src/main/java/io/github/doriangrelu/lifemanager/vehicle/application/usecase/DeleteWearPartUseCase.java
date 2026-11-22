package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.DeleteWearPart;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.WearPartCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.WearPartRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.WearPartAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Supprime définitivement une pièce d'usure (erreur de saisie). */
@Singleton
class DeleteWearPartUseCase implements DeleteWearPart {

    private final WearPartAccess access;
    private final WearPartRepository parts;

    DeleteWearPartUseCase(WearPartAccess access, WearPartRepository parts) {
        this.access = access;
        this.parts = parts;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public void delete(WearPartCommand command) {
        var vehicle = access.vehicleOf(command);
        parts.delete(access.partOf(vehicle, command));
    }
}
