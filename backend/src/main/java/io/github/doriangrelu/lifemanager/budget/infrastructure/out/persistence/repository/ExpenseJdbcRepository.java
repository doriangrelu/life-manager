package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.ExpenseEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Accès SQL aux dépenses (datasource du domaine budget). */
@Repository("budget")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ExpenseJdbcRepository extends CrudRepository<ExpenseEntity, UUID> {

    /** Dépenses du foyer entre deux jours inclus, la plus récente en premier. */
    @Query("""
            SELECT * FROM expense
            WHERE household_id = :householdId AND spent_on BETWEEN :firstDay AND :lastDay
            ORDER BY spent_on DESC, recorded_at DESC""")
    List<ExpenseEntity> findForPeriod(UUID householdId, LocalDate firstDay, LocalDate lastDay);
}
