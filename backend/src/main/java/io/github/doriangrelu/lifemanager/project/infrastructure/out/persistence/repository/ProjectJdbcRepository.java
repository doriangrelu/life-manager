package io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.entity.ProjectEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL aux projets (datasource du domaine projets). */
@Repository("project")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ProjectJdbcRepository extends CrudRepository<ProjectEntity, UUID> {

    @Query("SELECT * FROM project WHERE household_id = :householdId ORDER BY created_at DESC, id")
    List<ProjectEntity> findAllByHousehold(UUID householdId);
}
