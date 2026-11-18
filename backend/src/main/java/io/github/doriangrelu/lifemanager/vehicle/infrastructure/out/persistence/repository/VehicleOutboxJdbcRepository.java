package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox.OutboxJdbcRepository;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

/** Table {@code outbox} du schéma vehicle (requêtes communes du kernel). */
@Repository("vehicle")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface VehicleOutboxJdbcRepository extends OutboxJdbcRepository {
}
