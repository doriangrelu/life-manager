package io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.bills.application.port.out.InvoiceRepository;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.Invoice;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.InvoiceId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceId;
import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.mapper.InvoiceMapper;
import io.github.doriangrelu.lifemanager.bills.infrastructure.out.persistence.repository.InvoiceJdbcRepository;
import jakarta.inject.Singleton;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Implémente le stockage des métadonnées des factures avec PostgreSQL (table invoice). */
@Singleton
class InvoiceRepositoryAdapter implements InvoiceRepository {

    private final InvoiceJdbcRepository table;

    InvoiceRepositoryAdapter(InvoiceJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<Invoice> findById(InvoiceId id) {
        return table.findById(id.value()).map(InvoiceMapper::toDomain);
    }

    @Override
    public List<Invoice> findAllByOccurrence(OccurrenceId occurrenceId) {
        return table.findAllByOccurrence(occurrenceId.value()).stream().map(InvoiceMapper::toDomain).toList();
    }

    @Override
    public int countByOccurrence(OccurrenceId occurrenceId) {
        return table.countByOccurrenceId(occurrenceId.value());
    }

    @Override
    public Map<OccurrenceId, Integer> countByOccurrences(Collection<OccurrenceId> occurrenceIds) {
        if (occurrenceIds.isEmpty()) {
            return Map.of(); // IN () est refusé par PostgreSQL
        }
        return table.countByOccurrences(occurrenceIds.stream().map(OccurrenceId::value).toList()).stream()
                .collect(Collectors.toMap(row -> new OccurrenceId(row.occurrenceId()), row -> (int) row.total()));
    }

    @Override
    public void save(Invoice invoice) {
        table.save(InvoiceMapper.toEntity(invoice));
    }

    @Override
    public void delete(Invoice invoice) {
        table.deleteById(invoice.id().value());
    }
}
