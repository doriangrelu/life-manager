package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.CorrectWearPart;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.WearPartCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.WearPartView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.WearPartRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.WearPartAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/** Corrige la pose d'une pièce d'usure (date, kilométrage, marque, intervalle) ; son type ne change pas. */
@Singleton
class CorrectWearPartUseCase implements CorrectWearPart {

    private final WearPartAccess access;
    private final WearPartRepository parts;
    private final Clock clock;

    CorrectWearPartUseCase(WearPartAccess access, WearPartRepository parts, Clock clock) {
        this.access = access;
        this.parts = parts;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public WearPartView correct(WearPartCommand command) {
        var details = WearPartAccess.details(command.input());
        var vehicle = access.vehicleOf(command);
        var part = access.partOf(vehicle, command);
        part.correct(details, clock);
        parts.save(part);
        return WearPartView.from(part, vehicle.details().mileage());
    }
}
