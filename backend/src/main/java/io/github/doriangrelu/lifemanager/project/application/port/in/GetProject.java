package io.github.doriangrelu.lifemanager.project.application.port.in;

import io.github.doriangrelu.lifemanager.project.application.port.in.command.ProjectCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.ProjectDetailsView;

/** Fiche d'un projet avec ses tâches. */
public interface GetProject {

    ProjectDetailsView get(ProjectCommand command);
}
