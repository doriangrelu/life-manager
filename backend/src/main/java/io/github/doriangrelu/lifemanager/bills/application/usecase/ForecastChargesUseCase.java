package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.ForecastCharges;
import io.github.doriangrelu.lifemanager.bills.application.port.in.query.ForecastQuery;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.ForecastItem;
import io.github.doriangrelu.lifemanager.bills.application.port.out.ChargeRepository;
import io.github.doriangrelu.lifemanager.bills.application.port.out.OccurrenceRepository;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeAccess;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.ChargeOccurrence;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.RecurringCharge;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Échéances encore attendues d'une période : celles déjà créées et ouvertes (à venir, à confirmer),
 * puis celles que le calendrier d'une charge active créera plus tard, au montant actuel de la charge.
 */
@Singleton
class ForecastChargesUseCase implements ForecastCharges {

    private static final String PLANNED = "PLANNED";

    private final ChargeAccess access;
    private final ChargeRepository charges;
    private final OccurrenceRepository occurrences;

    ForecastChargesUseCase(ChargeAccess access, ChargeRepository charges, OccurrenceRepository occurrences) {
        this.access = access;
        this.charges = charges;
        this.occurrences = occurrences;
    }

    @Override
    @Transactional(transactionManager = "bills", readOnly = true)
    public List<ForecastItem> forecast(ForecastQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        query.ensureValidPeriod();
        return charges.findAllByHousehold(household).stream()
                .flatMap(charge -> itemsOf(charge, query.from(), query.to()))
                .sorted(Comparator.comparing(ForecastItem::dueOn).thenComparing(ForecastItem::name))
                .toList();
    }

    private Stream<ForecastItem> itemsOf(RecurringCharge charge, LocalDate from, LocalDate to) {
        var known = occurrences.findAllByCharge(charge.id());
        var open = known.stream()
                .filter(occurrence -> occurrence.settlement().status().isOpen())
                .filter(occurrence -> isBetween(occurrence.due().dueOn(), from, to))
                .map(occurrence -> stored(charge, occurrence));
        return Stream.concat(open, planned(charge, known, from, to));
    }

    // Échéances futures pas encore créées : après la dernière connue, seulement pour une charge active.
    private static Stream<ForecastItem> planned(RecurringCharge charge, List<ChargeOccurrence> known, LocalDate from,
                                                LocalDate to) {
        if (!charge.active()) {
            return Stream.empty();
        }
        var lastKnown = known.stream().map(occurrence -> occurrence.due().dueOn())
                .max(Comparator.naturalOrder()).orElse(LocalDate.MIN);
        return charge.schedule().dueDatesBetween(from, to).stream()
                .filter(dueOn -> dueOn.isAfter(lastKnown))
                .map(dueOn -> item(charge, null, dueOn, charge.details().amount().cents(), PLANNED));
    }

    private static ForecastItem stored(RecurringCharge charge, ChargeOccurrence occurrence) {
        return item(charge, occurrence, occurrence.due().dueOn(), occurrence.due().expected().cents(),
                occurrence.settlement().status().name());
    }

    private static ForecastItem item(RecurringCharge charge, ChargeOccurrence occurrence, LocalDate dueOn,
                                     long cents, String status) {
        var details = charge.details();
        return new ForecastItem(occurrence == null ? null : occurrence.id().value(), charge.id().value(),
                details.name().value(), dueOn, cents, status, details.direction().name(), details.budgetCategoryId());
    }

    private static boolean isBetween(LocalDate day, LocalDate from, LocalDate to) {
        return !day.isBefore(from) && !day.isAfter(to);
    }
}
