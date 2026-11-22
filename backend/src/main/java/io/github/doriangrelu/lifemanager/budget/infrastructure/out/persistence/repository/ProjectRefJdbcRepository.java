package io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.budget.infrastructure.out.persistence.entity.ProjectRefEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL à la copie locale des projets (datasource du domaine budget). */
@Repository("budget")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ProjectRefJdbcRepository extends CrudRepository<ProjectRefEntity, UUID> {

    @Query("SELECT project_id FROM project_ref WHERE household_id = :householdId AND NOT counts_in_budget")
    List<UUID> findOffBudget(UUID householdId);

    /** Ajoute ou remplace : un même événement peut arriver deux fois. */
    @Query("""
            INSERT INTO project_ref (project_id, household_id, name, status, counts_in_budget)
            VALUES (:projectId, :householdId, :name, :status, :countsInBudget)
            ON CONFLICT (project_id) DO UPDATE
            SET name = EXCLUDED.name, status = EXCLUDED.status, counts_in_budget = EXCLUDED.counts_in_budget""")
    void upsert(UUID projectId, UUID householdId, String name, String status, boolean countsInBudget);
}
