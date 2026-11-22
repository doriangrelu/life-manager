package io.github.doriangrelu.lifemanager.project.application.port.in.result;

import java.util.List;

/** Fiche d'un projet : le projet et ses tâches dans l'ordre. */
public record ProjectDetailsView(ProjectView project, List<TaskView> tasks) {
}
