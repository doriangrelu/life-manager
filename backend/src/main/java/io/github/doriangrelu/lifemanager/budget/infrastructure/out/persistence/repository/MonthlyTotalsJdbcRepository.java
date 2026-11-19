package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.ExpenseEntity;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.projection.CategoryTotalRow;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.projection.MemberTotalRow;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Sommes des dépenses d'un foyer entre deux jours inclus, calculées par PostgreSQL.
 * {@code ::bigint} : la somme de BIGINT est un NUMERIC en PostgreSQL, on la ramène à un entier de centimes.
 */
@Repository("budget")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MonthlyTotalsJdbcRepository extends GenericRepository<ExpenseEntity, UUID> {

    @Query("""
            SELECT c.id AS category_id, c.name AS name, SUM(e.amount_cents)::bigint AS total_cents
            FROM expense e JOIN category c ON c.id = e.category_id
            WHERE e.household_id = :householdId AND e.spent_on BETWEEN :firstDay AND :lastDay
            GROUP BY c.id, c.name
            ORDER BY total_cents DESC, lower(c.name)""")
    List<CategoryTotalRow> totalsByCategory(UUID householdId, LocalDate firstDay, LocalDate lastDay);

    @Query("""
            SELECT paid_by AS member_id, SUM(amount_cents)::bigint AS total_cents
            FROM expense
            WHERE household_id = :householdId AND spent_on BETWEEN :firstDay AND :lastDay
            GROUP BY paid_by
            ORDER BY total_cents DESC""")
    List<MemberTotalRow> totalsByPayer(UUID householdId, LocalDate firstDay, LocalDate lastDay);

    /** Bénéficiaire {@code null} = dépenses communes. */
    @Query("""
            SELECT beneficiary AS member_id, SUM(amount_cents)::bigint AS total_cents
            FROM expense
            WHERE household_id = :householdId AND spent_on BETWEEN :firstDay AND :lastDay
            GROUP BY beneficiary
            ORDER BY total_cents DESC""")
    List<MemberTotalRow> totalsByBeneficiary(UUID householdId, LocalDate firstDay, LocalDate lastDay);

    /** Total des dépenses nées d'une dépense récurrente (liées à une échéance de charge). */
    @Query("""
            SELECT COALESCE(SUM(e.amount_cents), 0)::bigint
            FROM expense e JOIN charge_expense link ON link.expense_id = e.id
            WHERE e.household_id = :householdId AND e.spent_on BETWEEN :firstDay AND :lastDay""")
    long recurringTotal(UUID householdId, LocalDate firstDay, LocalDate lastDay);
}
