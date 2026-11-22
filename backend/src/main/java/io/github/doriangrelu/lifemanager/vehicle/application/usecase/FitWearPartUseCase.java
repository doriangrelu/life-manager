package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.FitWearPart;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.WearPartCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.WearPartView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.WearPartRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleFollowUp;
import io.github.doriangrelu.lifemanager.vehicle.application.service.WearPartAccess;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.WearPart;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.WearPartKind;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;

/**
 * Pose une pièce d'usure : la pièce du même type déjà montée est déposée (historique, km faits) et le
 * kilométrage actuel du véhicule est relevé s'il est dépassé.
 */
@Singleton
class FitWearPartUseCase implements FitWearPart {

    private final WearPartAccess access;
    private final WearPartRepository parts;
    private final VehicleFollowUp followUp;
    private final Clock clock;

    FitWearPartUseCase(WearPartAccess access, WearPartRepository parts, VehicleFollowUp followUp, Clock clock) {
        this.access = access;
        this.parts = parts;
        this.followUp = followUp;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public WearPartView fit(WearPartCommand command) {
        var kind = WearPartKind.of(command.input().kind());
        var details = WearPartAccess.details(command.input());
        var vehicle = access.vehicleOf(command);
        var part = WearPart.fit(WearPartId.newId(), vehicle.id(), kind, details, clock);
        parts.findMounted(vehicle.id(), kind).ifPresent(previous -> {
            previous.replaceWith(details);
            parts.save(previous); // déposée avant la pose : une seule pièce montée par type
        });
        parts.save(part);
        followUp.raiseMileage(vehicle, details.fittedKm());
        return WearPartView.from(part, vehicle.details().mileage());
    }
}
