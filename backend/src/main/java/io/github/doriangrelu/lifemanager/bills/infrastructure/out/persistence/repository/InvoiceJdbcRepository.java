package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.repository;

import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.entity.InvoiceEntity;
import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.projection.InvoiceCountRow;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/** Accès SQL aux factures (datasource du domaine des charges). */
@Repository("bills")
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface InvoiceJdbcRepository extends CrudRepository<InvoiceEntity, UUID> {

    @Query("SELECT * FROM invoice WHERE occurrence_id = :occurrenceId ORDER BY added_at, id")
    List<InvoiceEntity> findAllByOccurrence(UUID occurrenceId);

    int countByOccurrenceId(UUID occurrenceId);

    /** Nombre de factures de plusieurs échéances en une requête (les échéances sans facture sont absentes). */
    @Query("""
            SELECT occurrence_id, count(*) AS total FROM invoice
            WHERE occurrence_id IN (:occurrenceIds) GROUP BY occurrence_id""")
    List<InvoiceCountRow> countByOccurrences(Collection<UUID> occurrenceIds);
}
