package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.PersonalReminderCommand;

/** Supprime un rappel saisi du foyer. */
public interface DeletePersonalReminder {

    void delete(PersonalReminderCommand command);
}
