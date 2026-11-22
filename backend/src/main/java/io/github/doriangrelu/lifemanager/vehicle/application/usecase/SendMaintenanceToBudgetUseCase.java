package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.SendMaintenanceToBudget;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.SendToBudgetCommand;
import io.github.doriangrelu.lifemanager.vehicle.application.service.MaintenanceEntryAccess;
import io.github.doriangrelu.lifemanager.vehicle.application.service.MaintenanceExpenseRecorder;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Ajoute au budget, une seule fois, le coût d'une opération déjà saisie dans le carnet. */
@Singleton
class SendMaintenanceToBudgetUseCase implements SendMaintenanceToBudget {

    private final VehicleAccess vehicles;
    private final MaintenanceEntryAccess entries;
    private final MaintenanceExpenseRecorder budget;

    SendMaintenanceToBudgetUseCase(VehicleAccess vehicles, MaintenanceEntryAccess entries,
                                   MaintenanceExpenseRecorder budget) {
        this.vehicles = vehicles;
        this.entries = entries;
        this.budget = budget;
    }

    @Override
    @Transactional(transactionManager = "vehicle")
    public void send(SendToBudgetCommand command) {
        var entry = entries.entryForMember(command.householdId(), command.vehicleId(), command.entryId(),
                command.requesterId());
        var vehicle = vehicles.loadForMember(command.householdId(), command.vehicleId(), command.requesterId());
        budget.send(entry, vehicle, budget.target(vehicle, command.budget()));
    }
}
