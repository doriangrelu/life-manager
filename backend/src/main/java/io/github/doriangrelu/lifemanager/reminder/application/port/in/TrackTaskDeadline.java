package io.github.doriangrelu.lifemanager.reminder.application.port.in;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.TrackTaskDeadlineCommand;

/** Suit la date d'une tâche de projet (ou l'oublie). */
public interface TrackTaskDeadline {

    void track(TrackTaskDeadlineCommand command);
}
