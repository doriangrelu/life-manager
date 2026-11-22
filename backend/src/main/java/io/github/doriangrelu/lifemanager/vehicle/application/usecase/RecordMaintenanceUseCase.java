package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.RecordMaintenance;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.RecordMaintenanceCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.MaintenanceEntryView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceEntryRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.MaintenanceExpenseRecorder;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleAccess;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleFollowUp;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleInputs;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceEntry;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.NextDeadline;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.Optional;

/**
 * Ajoute une opération au carnet d'un véhicule du foyer. Un contrôle technique ou une révision reporte aussi
 * l'échéance du véhicule, dans la même transaction, sauf si le carnet en contient déjà un plus récent.
 * Avec le choix « Ajouter au budget », son coût devient une dépense.
 */
@Singleton
class RecordMaintenanceUseCase implements RecordMaintenance {

    private final VehicleAccess access;
    private final MaintenanceEntryRepository entries;
    private final VehicleFollowUp followUp;
    private final MaintenanceExpenseRecorder budget;
    private final Clock clock;

    RecordMaintenanceUseCase(VehicleAccess access, MaintenanceEntryRepository entries, VehicleFollowUp followUp,
                             MaintenanceExpenseRecorder budget, Clock clock) {
        this.access = access;
        this.entries = entries;
        this.followUp = followUp;
        this.budget = budget;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public MaintenanceEntryView record(RecordMaintenanceCommand command) {
        var details = VehicleInputs.details(command.input());
        // Vérifiée avant tout enregistrement : une date précise invalide ne laisse rien en base.
        var next = NextDeadline.after(details, command.input().nextDueOn());
        var vehicle = access.loadForMember(command.householdId(), command.vehicleId(), command.requesterId());
        var target = Optional.ofNullable(command.budget()).map(input -> budget.target(vehicle, input));
        var entry = MaintenanceEntry.record(MaintenanceEntryId.newId(), vehicle.id(), details,
                MemberId.of(command.requesterId()), clock);
        entries.save(entry);
        followUp.afterMaintenance(vehicle, details, next);
        target.ifPresent(chosen -> budget.send(entry, vehicle, chosen));
        return MaintenanceEntryView.from(entry, 0, target.isPresent());
    }
}
