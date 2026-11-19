package io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.reminder.infrastructure.out.persistence.entity.ReminderDoneEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Accès SQL à l'historique des rappels faits (datasource du domaine rappels). */
@Repository("reminder")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ReminderDoneJdbcRepository extends CrudRepository<ReminderDoneEntity, UUID> {

    @Query("SELECT * FROM reminder_done WHERE household_id = :householdId ORDER BY done_at DESC, id"
            + " LIMIT :limit OFFSET :offset")
    List<ReminderDoneEntity> findPage(UUID householdId, int limit, int offset);

    @Query("DELETE FROM reminder_done WHERE done_at < :moment")
    void deleteDoneBefore(Instant moment);
}
