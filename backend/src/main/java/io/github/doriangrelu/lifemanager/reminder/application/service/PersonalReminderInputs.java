package io.github.doriangrelu.lifemanager.reminder.application.service;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.PersonalReminderInput;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.NoticePeriod;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.PersonalReminderDetails;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.Recurrence;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderNote;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.ReminderTitle;

/** Transforme la saisie d'un rappel en objets du domaine, partagée par l'ajout et la modification. */
public final class PersonalReminderInputs {

    private PersonalReminderInputs() {
    }

    public static PersonalReminderDetails details(PersonalReminderInput input) {
        return new PersonalReminderDetails(new ReminderTitle(input.title()), ReminderNote.ofNullable(input.note()),
                input.dueOn(), Recurrence.of(input.recurrence()), NoticePeriod.of(input.notice()));
    }
}
