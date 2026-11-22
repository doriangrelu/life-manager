package io.github.doriangrelu.lifemanager.project.application.usecase;

import io.github.doriangrelu.lifemanager.project.application.port.in.DeleteProject;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.ProjectCommand;
import io.github.doriangrelu.lifemanager.project.application.port.out.ProjectRepository;
import io.github.doriangrelu.lifemanager.project.application.port.out.TaskRepository;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectAccess;
import io.github.doriangrelu.lifemanager.project.application.service.TaskAnnouncer;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

/** Supprime un projet et ses tâches ; ses dépenses restent dans le budget, sans projet. */
@Singleton
class DeleteProjectUseCase implements DeleteProject {

    private final ProjectAccess loader;
    private final ProjectRepository projects;
    private final TaskRepository tasks;
    private final TaskAnnouncer announcer;

    DeleteProjectUseCase(ProjectAccess loader, ProjectRepository projects, TaskRepository tasks,
                         TaskAnnouncer announcer) {
        this.loader = loader;
        this.projects = projects;
        this.tasks = tasks;
        this.announcer = announcer;
    }

    @Override
    @Transactional(transactionManager = "project")
    public void delete(ProjectCommand command) {
        var project = loader.project(command.householdId(), command.projectId(), command.requesterId());
        announcer.forget(project, tasks.findAllByProject(project.id()));
        tasks.deleteAllByProject(project.id());
        project.delete(announcer.now());
        projects.delete(project.id());
        announcer.publish(project);
    }
}
