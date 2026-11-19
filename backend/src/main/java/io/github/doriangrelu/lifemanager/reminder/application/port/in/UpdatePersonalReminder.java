package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.UpdatePersonalReminderCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.result.PersonalReminderView;

/** Modifie un rappel saisi du foyer. */
public interface UpdatePersonalReminder {

    PersonalReminderView update(UpdatePersonalReminderCommand command);
}
