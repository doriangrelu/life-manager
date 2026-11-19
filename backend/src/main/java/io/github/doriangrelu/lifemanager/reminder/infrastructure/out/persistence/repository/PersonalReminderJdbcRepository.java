package io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.entity.PersonalReminderEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL aux rappels saisis (datasource du domaine rappels). */
@Repository("reminder")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PersonalReminderJdbcRepository extends CrudRepository<PersonalReminderEntity, UUID> {

    @Query("SELECT * FROM personal_reminder WHERE household_id = :householdId ORDER BY due_on, lower(title), id")
    List<PersonalReminderEntity> findAllByHousehold(UUID householdId);
}
