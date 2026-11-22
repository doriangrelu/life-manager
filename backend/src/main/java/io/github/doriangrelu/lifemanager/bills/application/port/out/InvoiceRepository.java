package io.github.doriangrelu.lifemanager.bills.application.port.out;

import io.github.doriangrelu.lifemanager.bills.domain.aggregate.Invoice;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.InvoiceId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceId;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Stockage des métadonnées des factures (le contenu est dans {@link InvoiceStorage}). */
public interface InvoiceRepository {

    Optional<Invoice> findById(InvoiceId id);

    List<Invoice> findAllByOccurrence(OccurrenceId occurrenceId);

    int countByOccurrence(OccurrenceId occurrenceId);

    /** Nombre de factures de chaque échéance, en une seule lecture (0 pour celles qui n'en ont pas). */
    Map<OccurrenceId, Integer> countByOccurrences(Collection<OccurrenceId> occurrenceIds);

    void save(Invoice invoice);

    void delete(Invoice invoice);
}
