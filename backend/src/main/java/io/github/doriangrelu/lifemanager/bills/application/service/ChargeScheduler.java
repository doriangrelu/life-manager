package io.github.doriangrelu.lifemanager.bills.application.service;

import io.github.doriangrelu.lifemanager.bills.application.port.out.ChargeRepository;
import io.github.doriangrelu.lifemanager.bills.application.port.out.OccurrenceRepository;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.ChargeOccurrence;
import io.github.doriangrelu.lifemanager.bills.domain.aggregate.RecurringCharge;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceStatus;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Fait avancer le calendrier d'une charge, dans la transaction de l'appelant :
 * <ol>
 *   <li>les échéances arrivées à date passent payées (montant fixe) ou à confirmer (montant variable) ;</li>
 *   <li>les échéances manquées pendant un arrêt sont rattrapées, puis la suivante est prévue
 *       (après la dernière échéance du calendrier, la charge est terminée) ;</li>
 *   <li>la prochaine échéance ouverte est annoncée aux rappels.</li>
 * </ol>
 * Idempotent : relancé le même jour, il ne crée rien de plus (et la base refuse deux échéances à la même date).
 */
@Singleton
public class ChargeScheduler {

    /** Garde-fou : jamais plus de 100 ans d'échéances mensuelles parcourues d'un coup. */
    private static final int MAX_STEPS = 1200;

    private final OccurrenceRepository occurrences;
    private final ChargeRepository charges;
    private final DomainEventPublisher events;
    private final Clock clock;

    ChargeScheduler(OccurrenceRepository occurrences, ChargeRepository charges, DomainEventPublisher events,
                    Clock clock) {
        this.occurrences = occurrences;
        this.charges = charges;
        this.events = events;
        this.clock = clock;
    }

    public void advance(RecurringCharge charge) {
        var today = LocalDate.now(clock);
        var known = new ArrayList<>(occurrences.findAllByCharge(charge.id()));
        known.stream()
                .filter(occurrence -> occurrence.settlement().status() == OccurrenceStatus.UPCOMING)
                .filter(occurrence -> !occurrence.due().dueOn().isAfter(today))
                .forEach(occurrence -> arrive(occurrence, charge));
        if (charge.active()) {
            planMissing(charge, known, today);
        }
        announce(charge, known);
    }

    /** Recalcule les échéances à venir (après une modification ou une résiliation). */
    public void reschedule(RecurringCharge charge) {
        occurrences.deleteUpcomingOfCharge(charge.id());
        advance(charge);
    }

    private void planMissing(RecurringCharge charge, List<ChargeOccurrence> known, LocalDate today) {
        // Une échéance future sautée ou payée d'avance ne compte pas : la suivante doit être prévue.
        if (known.stream().anyMatch(occurrence -> occurrence.due().dueOn().isAfter(today)
                && occurrence.settlement().status() == OccurrenceStatus.UPCOMING)) {
            return; // la prochaine échéance est déjà prévue
        }
        var last = known.stream().map(occurrence -> occurrence.due().dueOn()).max(Comparator.naturalOrder())
                .orElse(null);
        for (int index = 0; index < MAX_STEPS; index++) {
            var dueOn = charge.schedule().dueOn(index);
            if (last != null && !dueOn.isAfter(last)) {
                continue;
            }
            if (charge.schedule().endsBefore(dueOn)) {
                finish(charge);
                return;
            }
            var occurrence = ChargeOccurrence.plan(OccurrenceId.newId(), charge, dueOn);
            known.add(occurrence);
            if (dueOn.isAfter(today)) {
                occurrences.save(occurrence);
                return;
            }
            arrive(occurrence, charge); // échéance manquée pendant un arrêt
        }
    }

    /** Dernière échéance passée (fin d'un crédit) : la charge se termine d'elle-même. */
    private void finish(RecurringCharge charge) {
        charge.complete();
        charges.save(charge);
    }

    private void arrive(ChargeOccurrence occurrence, RecurringCharge charge) {
        occurrence.arrive(charge, clock);
        occurrences.save(occurrence);
        events.publishEventsOf(occurrence);
    }

    /** Prochaine échéance ouverte : la plus ancienne encore à confirmer ou à venir. */
    private void announce(RecurringCharge charge, List<ChargeOccurrence> known) {
        var next = known.stream()
                .filter(occurrence -> occurrence.settlement().status().isOpen())
                .min(Comparator.comparing(occurrence -> occurrence.due().dueOn()));
        charge.announceDeadline(next.map(occurrence -> occurrence.due().dueOn()).orElse(null),
                next.map(occurrence -> occurrence.settlement().status() == OccurrenceStatus.TO_CONFIRM).orElse(false),
                clock);
        events.publishEventsOf(charge);
    }
}
