package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.RemoveReminderDoneCommand;

/** Retire une ligne de l'historique des rappels faits. */
public interface RemoveReminderDone {

    void remove(RemoveReminderDoneCommand command);
}
