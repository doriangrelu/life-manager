package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity.MaintenanceEntryEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL au carnet d'entretien (datasource du domaine véhicule). */
@Repository("vehicle")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MaintenanceEntryJdbcRepository extends CrudRepository<MaintenanceEntryEntity, UUID> {

    @Query("""
            SELECT * FROM maintenance_entry WHERE vehicle_id = :vehicleId
            ORDER BY performed_on DESC, recorded_at DESC""")
    List<MaintenanceEntryEntity> findAllByVehicle(UUID vehicleId);
}
