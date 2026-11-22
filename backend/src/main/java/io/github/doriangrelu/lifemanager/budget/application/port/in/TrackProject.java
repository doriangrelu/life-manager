package io.github.doriangrelu.lifemanager.budget.application.port.in;

import io.github.doriangrelu.lifemanager.budget.application.port.in.command.TrackProjectCommand;

/** Tient à jour la copie locale d'un projet. */
public interface TrackProject {

    void track(TrackProjectCommand command);
}
