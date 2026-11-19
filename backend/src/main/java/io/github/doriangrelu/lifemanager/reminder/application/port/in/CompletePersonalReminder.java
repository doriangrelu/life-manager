package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.PersonalReminderCommand;

/** « C'est fait » : un rappel ponctuel disparaît, un rappel récurrent passe à sa date suivante. */
public interface CompletePersonalReminder {

    void complete(PersonalReminderCommand command);
}
