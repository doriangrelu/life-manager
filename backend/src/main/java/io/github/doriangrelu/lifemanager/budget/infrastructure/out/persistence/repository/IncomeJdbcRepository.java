package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.IncomeEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Accès SQL aux rentrées d'argent (datasource du domaine budget). */
@Repository("budget")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface IncomeJdbcRepository extends CrudRepository<IncomeEntity, UUID> {

    /** Rentrées du foyer entre deux jours inclus, la plus récente en premier. */
    @Query("""
            SELECT * FROM income
            WHERE household_id = :householdId AND received_on BETWEEN :firstDay AND :lastDay
            ORDER BY received_on DESC, recorded_at DESC""")
    List<IncomeEntity> findForPeriod(UUID householdId, LocalDate firstDay, LocalDate lastDay);
}
