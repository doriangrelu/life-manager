package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.AddPersonalReminderCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.PersonalReminderView;

/** Ajoute un rappel saisi au foyer. */
public interface AddPersonalReminder {

    PersonalReminderView add(AddPersonalReminderCommand command);
}
