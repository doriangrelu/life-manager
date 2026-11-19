package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.IncomeEntity;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.projection.CategoryTotalRow;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.projection.MemberTotalRow;
import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.projection.MonthTotalRow;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Sommes des rentrées d'un foyer entre deux jours inclus, et résultat mois par mois, calculés par PostgreSQL.
 * {@code ::bigint} : la somme de BIGINT est un NUMERIC en PostgreSQL, on la ramène à un entier de centimes.
 */
@Repository("budget")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface IncomeTotalsJdbcRepository extends GenericRepository<IncomeEntity, UUID> {

    @Query("""
            SELECT c.id AS category_id, c.name AS name, SUM(i.amount_cents)::bigint AS total_cents
            FROM income i JOIN category c ON c.id = i.category_id
            WHERE i.household_id = :householdId AND i.received_on BETWEEN :firstDay AND :lastDay
            GROUP BY c.id, c.name
            ORDER BY total_cents DESC, lower(c.name)""")
    List<CategoryTotalRow> totalsByCategory(UUID householdId, LocalDate firstDay, LocalDate lastDay);

    /** Bénéficiaire {@code null} = rentrées du foyer entier. */
    @Query("""
            SELECT received_by AS member_id, SUM(amount_cents)::bigint AS total_cents
            FROM income
            WHERE household_id = :householdId AND received_on BETWEEN :firstDay AND :lastDay
            GROUP BY received_by
            ORDER BY total_cents DESC""")
    List<MemberTotalRow> totalsByReceiver(UUID householdId, LocalDate firstDay, LocalDate lastDay);

    /** Rentrées et dépenses de chaque mois qui en a au moins une, du plus ancien au plus récent. */
    @Query("""
            SELECT to_char(day, 'YYYY-MM') AS month,
                   SUM(income)::bigint AS income_cents, SUM(expense)::bigint AS expense_cents
            FROM (SELECT received_on AS day, amount_cents AS income, 0 AS expense FROM income
                  WHERE household_id = :householdId AND received_on BETWEEN :firstDay AND :lastDay
                  UNION ALL
                  SELECT spent_on AS day, 0 AS income, amount_cents AS expense FROM expense
                  WHERE household_id = :householdId AND spent_on BETWEEN :firstDay AND :lastDay) AS flows
            GROUP BY month
            ORDER BY month""")
    List<MonthTotalRow> monthlyTotals(UUID householdId, LocalDate firstDay, LocalDate lastDay);
}
