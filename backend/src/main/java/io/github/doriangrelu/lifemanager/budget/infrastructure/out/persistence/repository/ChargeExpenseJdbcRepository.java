package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.ChargeExpenseEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;

import java.util.Optional;
import java.util.UUID;

/** Accès SQL aux correspondances échéance → dépense (datasource du domaine budget). */
@Repository("budget")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ChargeExpenseJdbcRepository extends GenericRepository<ChargeExpenseEntity, UUID> {

    /** Réserve l'échéance ; retourne 0 si elle l'était déjà (sans erreur, la transaction reste utilisable). */
    @Query("""
            INSERT INTO charge_expense (occurrence_id, expense_id, household_id)
            VALUES (:occurrenceId, :expenseId, :householdId)
            ON CONFLICT (occurrence_id) DO NOTHING""")
    long insertIfAbsent(UUID occurrenceId, UUID expenseId, UUID householdId);

    Optional<ChargeExpenseEntity> findById(UUID occurrenceId);
}
