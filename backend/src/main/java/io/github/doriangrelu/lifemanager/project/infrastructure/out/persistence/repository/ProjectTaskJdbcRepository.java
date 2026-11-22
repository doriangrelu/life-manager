package io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.entity.ProjectTaskEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL aux tâches de projet (datasource du domaine projets). */
@Repository("project")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ProjectTaskJdbcRepository extends CrudRepository<ProjectTaskEntity, UUID> {

    @Query("SELECT * FROM project_task WHERE project_id = :projectId ORDER BY position, id")
    List<ProjectTaskEntity> findAllByProject(UUID projectId);

    @Query("SELECT * FROM project_task WHERE household_id = :householdId")
    List<ProjectTaskEntity> findAllByHousehold(UUID householdId);

    @Query("SELECT COALESCE(MAX(position) + 1, 0) FROM project_task WHERE project_id = :projectId")
    int nextPosition(UUID projectId);

    @Query("DELETE FROM project_task WHERE project_id = :projectId")
    void deleteAllByProject(UUID projectId);
}
