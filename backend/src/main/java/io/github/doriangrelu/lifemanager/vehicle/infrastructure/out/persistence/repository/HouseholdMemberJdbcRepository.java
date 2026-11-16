package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity.HouseholdMemberEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;

import java.util.UUID;

/** Accès SQL à la copie locale des membres de foyer (datasource du domaine véhicule). */
@Repository("vehicle")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface HouseholdMemberJdbcRepository extends GenericRepository<HouseholdMemberEntity, UUID> {

    boolean existsByHouseholdIdAndMemberId(UUID householdId, UUID memberId);

    /** Insère l'appartenance si elle n'existe pas encore : un même événement peut arriver deux fois. */
    @Query("""
            INSERT INTO household_member (id, household_id, member_id)
            VALUES (:id, :householdId, :memberId)
            ON CONFLICT (household_id, member_id) DO NOTHING""")
    void insertIfAbsent(UUID id, UUID householdId, UUID memberId);
}
