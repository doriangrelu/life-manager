package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.entity.MembershipEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL à la table des appartenances (datasource du domaine household). */
@Repository("household")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MembershipJdbcRepository extends CrudRepository<MembershipEntity, UUID> {

    List<MembershipEntity> findByHouseholdIdOrderByJoinedAt(UUID householdId);

    long countByMemberId(UUID memberId);

    boolean existsByHouseholdIdAndMemberId(UUID householdId, UUID memberId);
}
