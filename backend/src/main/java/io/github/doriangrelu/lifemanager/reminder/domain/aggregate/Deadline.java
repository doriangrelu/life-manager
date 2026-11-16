package io.github.doriangrelu.lifemanager.reminder.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.DeadlineDetails;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.DeadlineId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Reminder;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderStatus;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.SourceId;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Échéance suivie pour un foyer (ex. contrôle technique de la Clio), copiée depuis le domaine d'origine.
 * Porte la règle de rappel : une échéance passée ou dans les {@value #REMINDER_WINDOW_DAYS} jours donne un rappel.
 */
public final class Deadline extends AggregateRoot<DeadlineId> {

    /** Nombre de jours avant l'échéance à partir duquel on la rappelle. */
    public static final int REMINDER_WINDOW_DAYS = 30;

    private final HouseholdId householdId;
    private final SourceId sourceId;
    private final DeadlineDetails details;

    private Deadline(DeadlineId id, HouseholdId householdId, SourceId sourceId, DeadlineDetails details) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer de l'échéance");
        this.sourceId = Require.notNull(sourceId, "L'origine de l'échéance");
        this.details = Require.notNull(details, "Le détail de l'échéance");
    }

    /** Nouvelle échéance suivie pour un objet du foyer. */
    public static Deadline track(HouseholdId householdId, SourceId sourceId, DeadlineDetails details) {
        return new Deadline(DeadlineId.newId(), householdId, sourceId, details);
    }

    /** Recharge une échéance depuis le stockage. */
    public static Deadline restore(DeadlineId id, HouseholdId householdId, SourceId sourceId,
                                   DeadlineDetails details) {
        return new Deadline(id, householdId, sourceId, details);
    }

    /** Le rappel à donner ce jour-là, ou rien si l'échéance est encore lointaine. */
    public Optional<Reminder> reminderOn(LocalDate today) {
        var daysLeft = ChronoUnit.DAYS.between(today, details.dueOn());
        if (daysLeft < 0) {
            return Optional.of(new Reminder(ReminderStatus.OVERDUE, daysLeft));
        }
        if (daysLeft <= REMINDER_WINDOW_DAYS) {
            return Optional.of(new Reminder(ReminderStatus.DUE_SOON, daysLeft));
        }
        return Optional.empty();
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public SourceId sourceId() {
        return sourceId;
    }

    public DeadlineDetails details() {
        return details;
    }
}
