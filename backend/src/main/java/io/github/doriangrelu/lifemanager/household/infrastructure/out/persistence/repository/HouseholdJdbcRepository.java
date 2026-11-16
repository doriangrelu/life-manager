package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.entity.HouseholdEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL à la table des foyers (datasource du domaine household). */
@Repository("household")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface HouseholdJdbcRepository extends CrudRepository<HouseholdEntity, UUID> {

    @Query("""
            SELECT h.* FROM household h
            JOIN membership m ON m.household_id = h.id
            WHERE m.member_id = :memberId
            ORDER BY h.created_at""")
    List<HouseholdEntity> findAllByMember(UUID memberId);
}
