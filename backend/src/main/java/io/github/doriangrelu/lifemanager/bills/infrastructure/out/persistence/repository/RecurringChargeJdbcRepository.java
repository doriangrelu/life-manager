package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.entity.RecurringChargeEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL aux charges (datasource du domaine des charges). */
@Repository("bills")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RecurringChargeJdbcRepository extends CrudRepository<RecurringChargeEntity, UUID> {

    /** Charges actives d'abord, puis par nom. */
    @Query("SELECT * FROM recurring_charge WHERE household_id = :householdId ORDER BY active DESC, name, id")
    List<RecurringChargeEntity> findAllByHousehold(UUID householdId);

    @Query("SELECT id FROM recurring_charge WHERE active")
    List<UUID> findActiveIds();
}
