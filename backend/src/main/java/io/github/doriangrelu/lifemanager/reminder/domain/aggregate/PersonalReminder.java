package io.github.doriangrelu.lifemanager.reminder.domain.aggregate;

import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.PersonalReminderDetails;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.PersonalReminderId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Recurrence;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Reminder;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderStatus;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Rappel saisi par un membre (« Ramonage », « Déclarer les impôts »), visible par tout le foyer.
 * Un rappel récurrent garde sa date de départ : ses dates suivantes en sont toujours calculées
 * (un rappel du 31 tombe le 28 février puis de nouveau le 31 mars).
 */
public final class PersonalReminder extends AggregateRoot<PersonalReminderId> {

    /** Garde-fou : jamais plus de 100 ans parcourus pour trouver la date suivante. */
    private static final int MAX_STEPS = 1200;

    private final HouseholdId householdId;
    private PersonalReminderDetails details;
    private LocalDate anchorOn;

    private PersonalReminder(PersonalReminderId id, HouseholdId householdId, PersonalReminderDetails details,
                             LocalDate anchorOn) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer du rappel");
        this.details = Require.notNull(details, "Le détail du rappel");
        this.anchorOn = Require.notNull(anchorOn, "La date de départ du rappel");
    }

    /** Nouveau rappel du foyer ; sa date est aussi la date de départ de sa récurrence. */
    public static PersonalReminder create(HouseholdId householdId, PersonalReminderDetails details) {
        return new PersonalReminder(PersonalReminderId.newId(), householdId, details, details.dueOn());
    }

    /** Recharge un rappel depuis le stockage. */
    public static PersonalReminder restore(PersonalReminderId id, HouseholdId householdId,
                                           PersonalReminderDetails details, LocalDate anchorOn) {
        return new PersonalReminder(id, householdId, details, anchorOn);
    }

    /** Change le rappel ; sa nouvelle date devient la date de départ de sa récurrence. */
    public void update(PersonalReminderDetails newDetails) {
        details = Require.notNull(newDetails, "Le détail du rappel");
        anchorOn = newDetails.dueOn();
    }

    /**
     * « C'est fait » : un rappel récurrent passe à sa date suivante, après sa date actuelle et après aujourd'hui.
     *
     * @return {@code false} pour un rappel ponctuel : il est terminé et doit être supprimé
     */
    public boolean complete(LocalDate today) {
        var months = details.recurrence().months();
        if (details.recurrence() == Recurrence.NONE) {
            return false;
        }
        var after = details.dueOn().isAfter(today) ? details.dueOn() : today;
        for (int step = 1; step < MAX_STEPS; step++) {
            var next = anchorOn.plusMonths((long) months * step);
            if (next.isAfter(after)) {
                details = details.on(next);
                return true;
            }
        }
        return false;
    }

    /** Le rappel à donner ce jour-là, ou rien si la date est encore plus loin que le délai choisi. */
    public Optional<Reminder> reminderOn(LocalDate today) {
        var daysLeft = ChronoUnit.DAYS.between(today, details.dueOn());
        if (daysLeft < 0) {
            return Optional.of(new Reminder(ReminderStatus.OVERDUE, daysLeft));
        }
        if (daysLeft <= details.notice().days()) {
            return Optional.of(new Reminder(ReminderStatus.DUE_SOON, daysLeft));
        }
        return Optional.empty();
    }

    public boolean belongsTo(HouseholdId household) {
        return householdId.equals(household);
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public PersonalReminderDetails details() {
        return details;
    }

    public LocalDate anchorOn() {
        return anchorOn;
    }
}
