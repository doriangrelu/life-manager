package io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.entity.HouseholdMemberEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;

import java.util.UUID;

/** Requêtes d'effacement par foyer (départ d'un membre, suppression du foyer) du schéma reminder. */
@Repository("reminder")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface HouseholdDataJdbcRepository extends GenericRepository<HouseholdMemberEntity, UUID> {

    @Query("DELETE FROM household_member WHERE household_id = :householdId AND member_id = :memberId")
    void deleteMember(UUID householdId, UUID memberId);

    @Query("DELETE FROM household_member WHERE household_id = :householdId")
    void deleteMembers(UUID householdId);

    @Query("DELETE FROM deadline WHERE household_id = :householdId")
    void erase1(UUID householdId);

    @Query("DELETE FROM personal_reminder WHERE household_id = :householdId")
    void erasePersonalReminders(UUID householdId);
}
