package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.vehicle.application.port.out.VehicleRepository;
import io.github.doriangrelu.lifemanager.vehicle.domain.aggregate.Vehicle;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.mapper.VehicleMapper;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository.VehicleJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

/** Implémente le stockage des véhicules avec PostgreSQL (table vehicle ; le carnet suit par cascade). */
@Singleton
class VehicleRepositoryAdapter implements VehicleRepository {

    private final VehicleJdbcRepository table;

    VehicleRepositoryAdapter(VehicleJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<Vehicle> findById(VehicleId id) {
        return table.findById(id.value()).map(VehicleMapper::toDomain);
    }

    @Override
    public List<Vehicle> findAllByHousehold(HouseholdId householdId) {
        return table.findAllByHousehold(householdId.value()).stream().map(VehicleMapper::toDomain).toList();
    }

    @Override
    public void save(Vehicle vehicle) {
        var entity = VehicleMapper.toEntity(vehicle);
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }

    @Override
    public void delete(Vehicle vehicle) {
        table.deleteById(vehicle.id().value());
    }
}
