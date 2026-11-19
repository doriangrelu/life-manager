package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.persistence.entity.HouseholdMemberEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;

import java.util.List;
import java.util.UUID;

/** Requêtes d'effacement par foyer (départ d'un membre, suppression du foyer) du schéma vehicle. */
@Repository("vehicle")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface HouseholdDataJdbcRepository extends GenericRepository<HouseholdMemberEntity, UUID> {

    @Query("DELETE FROM household_member WHERE household_id = :householdId AND member_id = :memberId")
    void deleteMember(UUID householdId, UUID memberId);

    @Query("DELETE FROM household_member WHERE household_id = :householdId")
    void deleteMembers(UUID householdId);

    @Query("DELETE FROM vehicle WHERE household_id = :householdId")
    void erase1(UUID householdId);

    /** Clés des fichiers du foyer (avant leur effacement en base). */
    @Query("""
            SELECT attachment.storage_key
            FROM maintenance_attachment attachment
            JOIN maintenance_entry entry ON entry.id = attachment.entry_id
            JOIN vehicle ON vehicle.id = entry.vehicle_id
            WHERE vehicle.household_id = :householdId""")
    List<UUID> storageKeys(UUID householdId);

    @Query("DELETE FROM authorship WHERE household_id = :householdId")
    void eraseAuthorship(UUID householdId);
}
