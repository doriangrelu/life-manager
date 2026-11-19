package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.HouseholdMemberEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;

import java.util.UUID;

/** Requêtes d'effacement par foyer (départ d'un membre, suppression du foyer) du schéma budget. */
@Repository("budget")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface HouseholdDataJdbcRepository extends GenericRepository<HouseholdMemberEntity, UUID> {

    @Query("DELETE FROM household_member WHERE household_id = :householdId AND member_id = :memberId")
    void deleteMember(UUID householdId, UUID memberId);

    @Query("DELETE FROM household_member WHERE household_id = :householdId")
    void deleteMembers(UUID householdId);

    @Query("""
            DELETE
            FROM charge_expense
            WHERE expense_id IN (SELECT id
            FROM expense
            WHERE household_id = :householdId)""")
    void erase1(UUID householdId);

    @Query("DELETE FROM expense WHERE household_id = :householdId")
    void erase2(UUID householdId);

    @Query("DELETE FROM category WHERE household_id = :householdId")
    void erase3(UUID householdId);

    @Query("DELETE FROM charge_income WHERE income_id IN (SELECT id FROM income WHERE household_id = :householdId)")
    void eraseIncomeLinks(UUID householdId);

    @Query("DELETE FROM income WHERE household_id = :householdId")
    void eraseIncomes(UUID householdId);

    @Query("DELETE FROM authorship WHERE household_id = :householdId")
    void eraseAuthorship(UUID householdId);
}
