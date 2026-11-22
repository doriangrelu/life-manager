package io.github.doriangrelu.lifemanager.project.application.usecase;

import io.github.doriangrelu.lifemanager.project.application.port.in.GetProject;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.ProjectCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.ProjectDetailsView;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.ProjectView;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.TaskView;
import io.github.doriangrelu.lifemanager.project.application.port.out.TaskRepository;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Fiche d'un projet du foyer avec ses tâches. */
@Singleton
class GetProjectUseCase implements GetProject {

    private final ProjectAccess loader;
    private final TaskRepository tasks;

    GetProjectUseCase(ProjectAccess loader, TaskRepository tasks) {
        this.loader = loader;
        this.tasks = tasks;
    }

    @Override
    @Transactional(transactionManager = "project", readOnly = true)
    public ProjectDetailsView get(ProjectCommand command) {
        var project = loader.project(command.householdId(), command.projectId(), command.requesterId());
        var own = tasks.findAllByProject(project.id());
        return new ProjectDetailsView(ProjectView.of(project, ListProjectsUseCase.done(own), own.size()),
                own.stream().map(TaskView::of).toList());
    }
}
