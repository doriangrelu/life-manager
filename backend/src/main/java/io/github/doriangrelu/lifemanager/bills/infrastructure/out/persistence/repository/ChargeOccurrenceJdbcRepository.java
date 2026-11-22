package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.entity.ChargeOccurrenceEntity;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/** Accès SQL aux échéances (datasource du domaine des charges). */
@Repository("bills")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ChargeOccurrenceJdbcRepository extends CrudRepository<ChargeOccurrenceEntity, UUID> {

    @Query("SELECT * FROM charge_occurrence WHERE charge_id = :chargeId ORDER BY due_on DESC")
    List<ChargeOccurrenceEntity> findAllByCharge(UUID chargeId);

    @Query("SELECT * FROM charge_occurrence WHERE household_id = :householdId ORDER BY due_on DESC")
    List<ChargeOccurrenceEntity> findAllByHousehold(UUID householdId);

    @Query("""
            SELECT * FROM charge_occurrence
            WHERE household_id = :householdId AND status IN ('UPCOMING', 'TO_CONFIRM')
            ORDER BY due_on""")
    List<ChargeOccurrenceEntity> findOpenByHousehold(UUID householdId);

    // Une échéance à venir qui porte déjà une facture est gardée (la facture y reste attachée).
    @Query("""
            DELETE FROM charge_occurrence WHERE charge_id = :chargeId AND status = 'UPCOMING'
            AND NOT EXISTS (SELECT 1 FROM invoice WHERE invoice.occurrence_id = charge_occurrence.id)""")
    void deleteUpcoming(UUID chargeId);
}
