package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.TrackVehicleDeadlines;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.TrackVehicleDeadlinesCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.DeadlineRepository;
import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.Deadline;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.DeadlineDetails;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.DeadlineKind;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.SourceId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Subject;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Remplace les échéances suivies d'un véhicule par son état actuel. Touche jusqu'à deux agrégats {@link Deadline} :
 * c'est le remplacement d'une copie, fait d'un bloc pour ne jamais exposer un état à moitié remplacé.
 */
@Singleton
class TrackVehicleDeadlinesUseCase implements TrackVehicleDeadlines {

    private final DeadlineRepository deadlines;

    TrackVehicleDeadlinesUseCase(DeadlineRepository deadlines) {
        this.deadlines = deadlines;
    }

    @Override
    @Transactional(transactionManager = "reminder")
    public void track(TrackVehicleDeadlinesCommand command) {
        var household = new HouseholdId(command.householdId());
        var source = new SourceId(command.vehicleId());
        var subject = new Subject(command.vehicleName());
        var tracked = new ArrayList<Deadline>();
        addIfDue(tracked, household, source, new Due(DeadlineKind.TECHNICAL_INSPECTION, subject,
                command.technicalInspectionDue()));
        addIfDue(tracked, household, source, new Due(DeadlineKind.SERVICE, subject, command.serviceDue()));
        deadlines.replaceForSource(source, tracked);
    }

    private static void addIfDue(List<Deadline> tracked, HouseholdId household, SourceId source, Due due) {
        if (due.date() != null) {
            tracked.add(Deadline.track(household, source, new DeadlineDetails(due.kind(), due.subject(), due.date())));
        }
    }

    /** Une échéance reçue, dont la date peut être absente. */
    private record Due(DeadlineKind kind, Subject subject, LocalDate date) {
    }
}
