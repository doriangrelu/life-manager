package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.TrackChargeDeadline;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.TrackChargeDeadlineCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.DeadlineRepository;
import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.Deadline;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.DeadlineDetails;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.DeadlineKind;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.SourceId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Subject;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Remplace l'échéance suivie d'une charge par son état actuel (ou l'oublie s'il n'y en a plus). */
@Singleton
class TrackChargeDeadlineUseCase implements TrackChargeDeadline {

    private final DeadlineRepository deadlines;

    TrackChargeDeadlineUseCase(DeadlineRepository deadlines) {
        this.deadlines = deadlines;
    }

    @Override
    @Transactional(transactionManager = "reminder")
    public void track(TrackChargeDeadlineCommand command) {
        var source = new SourceId(command.chargeId());
        if (command.nextDueOn() == null) {
            deadlines.replaceForSource(source, List.of());
            return;
        }
        var kind = command.awaitingConfirmation() ? DeadlineKind.CHARGE_TO_CONFIRM : DeadlineKind.CHARGE;
        var details = new DeadlineDetails(kind, new Subject(command.chargeName()), command.nextDueOn());
        deadlines.replaceForSource(source, List.of(Deadline.track(new HouseholdId(command.householdId()), source,
                details)));
    }
}
