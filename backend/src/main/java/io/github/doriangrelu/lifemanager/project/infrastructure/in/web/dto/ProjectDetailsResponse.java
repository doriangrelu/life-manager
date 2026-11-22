package io.github.doriangrelu.lifemanager.project.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.project.application.port.in.result.ProjectDetailsView;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

/** Fiche d'un projet : le projet et ses tâches dans l'ordre. */
@Serdeable
public record ProjectDetailsResponse(ProjectResponse project, List<TaskResponse> tasks) {

    public static ProjectDetailsResponse from(ProjectDetailsView view) {
        return new ProjectDetailsResponse(ProjectResponse.from(view.project()),
                view.tasks().stream().map(TaskResponse::from).toList());
    }
}
