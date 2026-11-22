package io.github.doriangrelu.lifemanager.project.application.port.in;

import io.github.doriangrelu.lifemanager.project.application.port.in.command.AddProjectCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.ProjectView;

/** Ajoute un projet au foyer. */
public interface AddProject {

    ProjectView add(AddProjectCommand command);
}
