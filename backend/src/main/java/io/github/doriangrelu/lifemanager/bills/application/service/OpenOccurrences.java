package io.github.doriangrelu.lifemanager.bills.application.service;

import io.github.doriangrelu.lifemanager.bills.application.port.in.result.ChargeView;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.OccurrenceView;
import io.github.doriangrelu.lifemanager.bills.application.port.out.InvoiceRepository;
import io.github.doriangrelu.lifemanager.bills.application.port.out.OccurrenceRepository;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.ChargeOccurrence;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.RecurringCharge;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.ChargeId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.HouseholdId;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Présentation des charges avec leur prochaine échéance ouverte (à confirmer ou à venir). */
@Singleton
public class OpenOccurrences {

    private final OccurrenceRepository occurrences;
    private final InvoiceRepository invoices;
    private final Clock clock;

    OpenOccurrences(OccurrenceRepository occurrences, InvoiceRepository invoices, Clock clock) {
        this.occurrences = occurrences;
        this.invoices = invoices;
        this.clock = clock;
    }

    /** Prochaine échéance ouverte d'une charge, ou {@code null}. */
    public OccurrenceView nextOf(RecurringCharge charge) {
        return occurrences.findAllByCharge(charge.id()).stream()
                .filter(occurrence -> occurrence.settlement().status().isOpen())
                .min(Comparator.comparing(occurrence -> occurrence.due().dueOn()))
                .map(this::view)
                .orElse(null);
    }

    /** Toutes les charges d'un foyer, chacune avec sa prochaine échéance ouverte. */
    public List<ChargeView> viewsOf(HouseholdId household, List<RecurringCharge> charges) {
        Map<ChargeId, ChargeOccurrence> next = occurrences.findOpenByHousehold(household).stream()
                .collect(Collectors.toMap(ChargeOccurrence::chargeId, Function.identity(),
                        (first, other) -> first.due().dueOn().isAfter(other.due().dueOn()) ? other : first));
        var counts = invoices.countByOccurrences(next.values().stream().map(ChargeOccurrence::id).toList());
        return charges.stream()
                .map(charge -> ChargeView.from(charge, Optional.ofNullable(next.get(charge.id()))
                        .map(occurrence -> OccurrenceView.from(occurrence, counts.getOrDefault(occurrence.id(), 0)))
                        .orElse(null)))
                .toList();
    }

    public OccurrenceView view(ChargeOccurrence occurrence) {
        return OccurrenceView.from(occurrence, invoices.countByOccurrence(occurrence.id()));
    }

    /** Plusieurs échéances avec leur nombre de factures, lu en une seule fois. */
    public List<OccurrenceView> views(List<ChargeOccurrence> list) {
        var counts = invoices.countByOccurrences(list.stream().map(ChargeOccurrence::id).toList());
        return list.stream().map(occurrence -> OccurrenceView.from(occurrence, counts.getOrDefault(occurrence.id(), 0)))
                .toList();
    }

    /** Horloge partagée par les use cases du domaine. */
    public Clock clock() {
        return clock;
    }
}
