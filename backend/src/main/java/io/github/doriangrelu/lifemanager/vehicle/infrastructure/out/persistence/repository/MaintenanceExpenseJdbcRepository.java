package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity.MaintenanceExpenseEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL aux opérations envoyées au budget (datasource du domaine véhicule). */
@Repository("vehicle")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MaintenanceExpenseJdbcRepository extends CrudRepository<MaintenanceExpenseEntity, UUID> {

    @Query("""
            SELECT e.* FROM maintenance_expense e
            JOIN maintenance_entry m ON m.id = e.entry_id
            WHERE m.vehicle_id = :vehicleId""")
    List<MaintenanceExpenseEntity> findAllOfVehicle(UUID vehicleId);
}
