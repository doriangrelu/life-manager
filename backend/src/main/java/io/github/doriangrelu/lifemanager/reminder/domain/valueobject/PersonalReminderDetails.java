package io.github.doriangrelu.lifemanager.reminder.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;

/**
 * Ce qu'un membre saisit sur un rappel, déjà vérifié dans sa forme.
 *
 * @param note   note, ou {@code null}
 * @param dueOn  date du rappel (la prochaine, pour un rappel récurrent)
 * @param notice à partir de quand prévenir avant cette date
 */
public record PersonalReminderDetails(ReminderTitle title, ReminderNote note, LocalDate dueOn, Recurrence recurrence,
                                      NoticePeriod notice) {

    public PersonalReminderDetails {
        Require.notNull(title, "Le titre du rappel");
        Require.notNull(dueOn, "La date du rappel");
        Require.notNull(recurrence, "La récurrence");
        Require.notNull(notice, "Le délai de rappel");
    }

    /** Mêmes informations, à une autre date (rappel récurrent passé à sa date suivante). */
    public PersonalReminderDetails on(LocalDate newDueOn) {
        return new PersonalReminderDetails(title, note, newDueOn, recurrence, notice);
    }
}
