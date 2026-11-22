package io.github.doriangrelu.lifemanager.project.application.port.in;

import io.github.doriangrelu.lifemanager.project.application.port.in.command.ProjectCommand;

/** Supprime un projet et ses tâches. */
public interface DeleteProject {

    void delete(ProjectCommand command);
}
