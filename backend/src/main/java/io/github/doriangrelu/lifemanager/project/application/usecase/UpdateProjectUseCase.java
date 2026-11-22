package io.github.doriangrelu.lifemanager.project.application.usecase;

import io.github.doriangrelu.lifemanager.project.application.port.in.UpdateProject;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.UpdateProjectCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.ProjectView;
import io.github.doriangrelu.lifemanager.project.application.port.out.ProjectRepository;
import io.github.doriangrelu.lifemanager.project.application.port.out.TaskRepository;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectInputs;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectAccess;
import io.github.doriangrelu.lifemanager.project.application.service.TaskAnnouncer;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Modifie un projet ; ses tâches datées suivent son nom et son état dans les rappels. */
@Singleton
class UpdateProjectUseCase implements UpdateProject {

    private final ProjectAccess loader;
    private final ProjectRepository projects;
    private final TaskRepository tasks;
    private final TaskAnnouncer announcer;

    UpdateProjectUseCase(ProjectAccess loader, ProjectRepository projects, TaskRepository tasks,
                         TaskAnnouncer announcer) {
        this.loader = loader;
        this.projects = projects;
        this.tasks = tasks;
        this.announcer = announcer;
    }

    @Override
    @Transactional(transactionManager = "project")
    public ProjectView update(UpdateProjectCommand command) {
        var project = loader.project(command.householdId(), command.projectId(), command.requesterId());
        project.update(ProjectInputs.details(command.input()), announcer.now());
        projects.save(project);
        announcer.publish(project);
        var own = tasks.findAllByProject(project.id());
        announcer.announce(project, own);
        return ProjectView.of(project, ListProjectsUseCase.done(own), own.size());
    }
}
