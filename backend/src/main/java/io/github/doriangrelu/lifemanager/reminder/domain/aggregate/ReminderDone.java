package io.github.doriangrelu.lifemanager.reminder.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Completion;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderDoneId;

import java.time.Instant;

/** Ligne de l'historique : un « c'est fait » sur un rappel saisi. Ne change plus une fois écrite. */
public final class ReminderDone extends AggregateRoot<ReminderDoneId> {

    private final HouseholdId householdId;
    private final Completion completion;

    private ReminderDone(ReminderDoneId id, HouseholdId householdId, Completion completion) {
        super(id);
        this.householdId = Require.notNull(householdId, "Le foyer de la ligne d'historique");
        this.completion = Require.notNull(completion, "Ce qui a été fait");
    }

    /** Trace du rappel tel qu'il était au moment du « c'est fait » (à appeler avant qu'il passe à la date suivante). */
    public static ReminderDone of(PersonalReminder reminder, MemberId doneBy, Instant doneAt) {
        var details = reminder.details();
        return new ReminderDone(ReminderDoneId.newId(), reminder.householdId(),
                new Completion(details.title(), details.dueOn(), doneAt, doneBy));
    }

    /** Recharge une ligne depuis le stockage. */
    public static ReminderDone restore(ReminderDoneId id, HouseholdId householdId, Completion completion) {
        return new ReminderDone(id, householdId, completion);
    }

    public boolean belongsTo(HouseholdId household) {
        return householdId.equals(household);
    }

    public HouseholdId householdId() {
        return householdId;
    }

    public Completion completion() {
        return completion;
    }
}
