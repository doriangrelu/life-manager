package io.github.doriangrelu.lifemanager.identity.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox.OutboxJdbcRepository;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

/** Table {@code outbox} du schéma identity (requêtes communes du kernel). */
@Repository("identity")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface IdentityOutboxJdbcRepository extends OutboxJdbcRepository {
}
