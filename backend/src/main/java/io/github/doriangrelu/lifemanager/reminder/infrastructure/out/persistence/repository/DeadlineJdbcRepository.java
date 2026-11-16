package io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.entity.DeadlineEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL aux échéances suivies (datasource du domaine rappels). */
@Repository("reminder")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DeadlineJdbcRepository extends CrudRepository<DeadlineEntity, UUID> {

    @Query("SELECT * FROM deadline WHERE household_id = :householdId ORDER BY due_on, kind, id")
    List<DeadlineEntity> findAllByHousehold(UUID householdId);

    @Query("DELETE FROM deadline WHERE source_id = :sourceId")
    void deleteBySource(UUID sourceId);
}
