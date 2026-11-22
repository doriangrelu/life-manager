package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceExpenseRepository;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceExpense;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.ExpenseTarget;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity.MaintenanceExpenseEntity;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository.MaintenanceExpenseJdbcRepository;
import jakarta.inject.Singleton;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Implémente le stockage des opérations envoyées au budget avec PostgreSQL (table maintenance_expense). */
@Singleton
class MaintenanceExpenseRepositoryAdapter implements MaintenanceExpenseRepository {

    private final MaintenanceExpenseJdbcRepository table;

    MaintenanceExpenseRepositoryAdapter(MaintenanceExpenseJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<MaintenanceExpense> findById(MaintenanceEntryId id) {
        return table.findById(id.value()).map(row -> MaintenanceExpense.restore(new MaintenanceEntryId(row.entryId()),
                new HouseholdId(row.householdId()), new ExpenseTarget(row.categoryId(), new MemberId(row.payerId()))));
    }

    @Override
    public Set<MaintenanceEntryId> findSentOfVehicle(VehicleId vehicleId) {
        return table.findAllOfVehicle(vehicleId.value()).stream()
                .map(row -> new MaintenanceEntryId(row.entryId()))
                .collect(Collectors.toSet());
    }

    @Override
    public void save(MaintenanceExpense expense) {
        var target = expense.target();
        table.save(new MaintenanceExpenseEntity(expense.id().value(), expense.householdId().value(),
                target.categoryId(), target.payer().value()));
    }

    @Override
    public void delete(MaintenanceExpense expense) {
        table.deleteById(expense.id().value());
    }
}
