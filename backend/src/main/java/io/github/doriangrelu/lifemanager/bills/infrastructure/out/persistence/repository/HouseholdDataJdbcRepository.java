package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.entity.HouseholdMemberEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;

import java.util.List;
import java.util.UUID;

/** Requêtes d'effacement par foyer (départ d'un membre, suppression du foyer) du schéma bills. */
@Repository("bills")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface HouseholdDataJdbcRepository extends GenericRepository<HouseholdMemberEntity, UUID> {

    @Query("DELETE FROM household_member WHERE household_id = :householdId AND member_id = :memberId")
    void deleteMember(UUID householdId, UUID memberId);

    @Query("DELETE FROM household_member WHERE household_id = :householdId")
    void deleteMembers(UUID householdId);

    @Query("""
            DELETE
            FROM invoice
            WHERE occurrence_id IN (SELECT id
            FROM charge_occurrence
            WHERE household_id = :householdId)""")
    void erase1(UUID householdId);

    @Query("DELETE FROM charge_occurrence WHERE household_id = :householdId")
    void erase2(UUID householdId);

    @Query("DELETE FROM recurring_charge WHERE household_id = :householdId")
    void erase3(UUID householdId);

    /** Clés des fichiers du foyer (avant leur effacement en base). */
    @Query("""
            SELECT invoice.storage_key
            FROM invoice
            JOIN charge_occurrence occurrence ON occurrence.id = invoice.occurrence_id
            WHERE occurrence.household_id = :householdId""")
    List<UUID> storageKeys(UUID householdId);

    @Query("""
            UPDATE recurring_charge SET payer_id = :ownerId
            WHERE household_id = :householdId AND payer_id = :memberId""")
    void reassignPayer(UUID householdId, UUID memberId, UUID ownerId);

    @Query("""
            UPDATE recurring_charge SET beneficiary_id = NULL
            WHERE household_id = :householdId AND beneficiary_id = :memberId""")
    void shareBeneficiary(UUID householdId, UUID memberId);

    @Query("DELETE FROM authorship WHERE household_id = :householdId")
    void eraseAuthorship(UUID householdId);
}
