package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity.WearPartEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Accès SQL aux pièces d'usure (datasource du domaine véhicule). */
@Repository("vehicle")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface WearPartJdbcRepository extends CrudRepository<WearPartEntity, UUID> {

    @Query("SELECT * FROM wear_part WHERE vehicle_id = :vehicleId ORDER BY fitted_on DESC, fitted_km DESC")
    List<WearPartEntity> findAllByVehicle(UUID vehicleId);

    @Query("SELECT * FROM wear_part WHERE vehicle_id = :vehicleId AND kind = :kind AND removed_on IS NULL")
    Optional<WearPartEntity> findMounted(UUID vehicleId, String kind);
}
