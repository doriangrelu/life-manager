package io.github.doriangrelu.lifemanager.bills.application.usecase;

import io.github.doriangrelu.lifemanager.bills.application.port.in.SkipOccurrence;
import io.github.doriangrelu.lifemanager.bills.application.port.in.command.OccurrenceCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.OccurrenceView;
import io.github.doriangrelu.lifemanager.bills.application.port.out.OccurrenceRepository;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeAccess;
import io.github.doriangrelu.lifemanager.bills.application.service.ChargeScheduler;
import io.github.doriangrelu.lifemanager.bills.application.service.OpenOccurrences;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Saute une échéance : aucune dépense, et la suivante est prévue. */
@Singleton
class SkipOccurrenceUseCase implements SkipOccurrence {

    private final ChargeAccess access;
    private final OccurrenceRepository occurrences;
    private final ChargeScheduler scheduler;
    private final OpenOccurrences open;

    SkipOccurrenceUseCase(ChargeAccess access, OccurrenceRepository occurrences, ChargeScheduler scheduler,
                          OpenOccurrences open) {
        this.access = access;
        this.occurrences = occurrences;
        this.scheduler = scheduler;
        this.open = open;
    }

    @Override
    @Transactional(transactionManager = "bills")
    public OccurrenceView skip(OccurrenceCommand command) {
        var charge = access.chargeForMember(command.householdId(), command.chargeId(), command.requesterId());
        var occurrence = access.occurrenceForMember(command);
        occurrence.skip();
        occurrences.save(occurrence);
        scheduler.advance(charge);
        return open.view(occurrence);
    }
}
