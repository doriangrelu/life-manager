package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity.VehicleEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL aux véhicules (datasource du domaine véhicule). */
@Repository("vehicle")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface VehicleJdbcRepository extends CrudRepository<VehicleEntity, UUID> {

    @Query("SELECT * FROM vehicle WHERE household_id = :householdId ORDER BY lower(name), id")
    List<VehicleEntity> findAllByHousehold(UUID householdId);
}
