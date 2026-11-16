package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.vehicle.application.port.out.MaintenanceEntryRepository;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.MaintenanceEntry;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.mapper.MaintenanceEntryMapper;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository.MaintenanceEntryJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

/** Implémente le stockage du carnet d'entretien avec PostgreSQL (table maintenance_entry). */
@Singleton
class MaintenanceEntryRepositoryAdapter implements MaintenanceEntryRepository {

    private final MaintenanceEntryJdbcRepository table;

    MaintenanceEntryRepositoryAdapter(MaintenanceEntryJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<MaintenanceEntry> findById(MaintenanceEntryId id) {
        return table.findById(id.value()).map(MaintenanceEntryMapper::toDomain);
    }

    @Override
    public List<MaintenanceEntry> findAllByVehicle(VehicleId vehicleId) {
        return table.findAllByVehicle(vehicleId.value()).stream().map(MaintenanceEntryMapper::toDomain).toList();
    }

    @Override
    public void save(MaintenanceEntry entry) {
        table.save(MaintenanceEntryMapper.toEntity(entry));
    }

    @Override
    public void delete(MaintenanceEntry entry) {
        table.deleteById(entry.id().value());
    }
}
