package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.TrackTaskDeadline;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.TrackTaskDeadlineCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.DeadlineRepository;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.reminder.domain.aggregate.Deadline;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.DeadlineDetails;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.DeadlineKind;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.SourceId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Subject;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Remplace l'échéance suivie d'une tâche de projet par sa date actuelle (ou l'oublie). */
@Singleton
class TrackTaskDeadlineUseCase implements TrackTaskDeadline {

    /** Le sujet affiché tient en 60 caractères : « titre · projet », raccourci si besoin. */
    private static final int SUBJECT_MAX = 60;

    private final DeadlineRepository deadlines;
    private final HouseholdMembers members;

    TrackTaskDeadlineUseCase(DeadlineRepository deadlines, HouseholdMembers members) {
        this.deadlines = deadlines;
        this.members = members;
    }

    @Override
    @Transactional(transactionManager = "reminder")
    public void track(TrackTaskDeadlineCommand command) {
        var source = new SourceId(command.taskId());
        var household = new HouseholdId(command.householdId());
        // Plus de date, ou foyer supprimé entre-temps (événement en retard) : aucun rappel.
        if (command.dueOn() == null || !members.isKnown(household)) {
            deadlines.replaceForSource(source, List.of());
            return;
        }
        var details = new DeadlineDetails(DeadlineKind.TASK, subject(command.title(), command.projectName()),
                command.dueOn());
        deadlines.replaceForSource(source, List.of(Deadline.track(household, source, details)));
    }

    static Subject subject(String title, String projectName) {
        var text = title.strip() + " · " + projectName.strip();
        return new Subject(text.length() <= SUBJECT_MAX ? text : text.substring(0, SUBJECT_MAX - 1) + "…");
    }
}
