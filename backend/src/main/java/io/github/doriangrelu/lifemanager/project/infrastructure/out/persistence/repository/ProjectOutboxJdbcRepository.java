package io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox.OutboxJdbcRepository;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

/** Table {@code outbox} du schéma project (requêtes communes du kernel). */
@Repository("project")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ProjectOutboxJdbcRepository extends OutboxJdbcRepository {
}
