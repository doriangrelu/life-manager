package io.github.doriangrelu.lifemanager.project.application.port.in;

import io.github.doriangrelu.lifemanager.project.application.port.in.command.UpdateProjectCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.ProjectView;

/** Modifie un projet. */
public interface UpdateProject {

    ProjectView update(UpdateProjectCommand command);
}
