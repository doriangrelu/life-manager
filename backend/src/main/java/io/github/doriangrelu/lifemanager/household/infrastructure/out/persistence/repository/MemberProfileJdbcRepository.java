package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.entity.MemberProfileEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/** Accès SQL à l'annuaire local des membres (datasource du domaine household). */
@Repository("household")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MemberProfileJdbcRepository extends CrudRepository<MemberProfileEntity, UUID> {

    List<MemberProfileEntity> findByMemberIdIn(Collection<UUID> memberIds);
}
