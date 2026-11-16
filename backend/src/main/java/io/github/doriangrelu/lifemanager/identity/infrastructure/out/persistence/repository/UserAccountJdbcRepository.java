package io.github.doriangrelu.lifemanager.identity.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.identity.infrastructure.out.persistence.entity.UserAccountEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.UUID;

/** Accès SQL à la table des profils, via la datasource du domaine identity. */
@Repository("identity")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface UserAccountJdbcRepository extends CrudRepository<UserAccountEntity, UUID> {
}
