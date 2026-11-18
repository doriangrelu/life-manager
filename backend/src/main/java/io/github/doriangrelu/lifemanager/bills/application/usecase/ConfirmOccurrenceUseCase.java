package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.ConfirmOccurrence;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.ConfirmOccurrenceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.OccurrenceView;
import io.github.doriangrelu.lifemanager.bills.application.port.out.OccurrenceRepository;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeAccess;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeScheduler;
import io.github.doriangrelu.lifemanager.bills.application.service.OpenOccurrences;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.Amount;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Paie une échéance à son montant réel ; le budget en fera une dépense. */
@Singleton
class ConfirmOccurrenceUseCase implements ConfirmOccurrence {

    private final ChargeAccess access;
    private final OccurrenceRepository occurrences;
    private final ChargeScheduler scheduler;
    private final DomainEventPublisher events;
    private final OpenOccurrences open;

    ConfirmOccurrenceUseCase(ChargeAccess access, OccurrenceRepository occurrences, ChargeScheduler scheduler,
                             DomainEventPublisher events, OpenOccurrences open) {
        this.access = access;
        this.occurrences = occurrences;
        this.scheduler = scheduler;
        this.events = events;
        this.open = open;
    }

    @Override
    @Transactional(transactionManager = "bills")
    public OccurrenceView confirm(ConfirmOccurrenceCommand command) {
        var target = command.target();
        var charge = access.chargeForMember(target.householdId(), target.chargeId(), target.requesterId());
        var occurrence = access.occurrenceForMember(target);
        occurrence.confirm(Amount.ofCents(command.amountCents()), charge, open.clock());
        occurrences.save(occurrence);
        events.publishEventsOf(occurrence);
        scheduler.advance(charge);
        return open.view(occurrence);
    }
}
