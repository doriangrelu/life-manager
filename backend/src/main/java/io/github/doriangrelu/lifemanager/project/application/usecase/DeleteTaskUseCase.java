package io.github.doriangrelu.lifemanager.project.application.usecase;

import io.github.doriangrelu.lifemanager.project.application.port.in.DeleteTask;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.TaskCommand;
import io.github.doriangrelu.lifemanager.project.application.port.out.TaskRepository;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectAccess;
import io.github.doriangrelu.lifemanager.project.application.service.TaskAnnouncer;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Supprime une tâche (et son rappel). */
@Singleton
class DeleteTaskUseCase implements DeleteTask {

    private final ProjectAccess loader;
    private final TaskRepository tasks;
    private final TaskAnnouncer announcer;

    DeleteTaskUseCase(ProjectAccess loader, TaskRepository tasks, TaskAnnouncer announcer) {
        this.loader = loader;
        this.tasks = tasks;
        this.announcer = announcer;
    }

    @Override
    @Transactional(transactionManager = "project")
    public void delete(TaskCommand command) {
        var project = loader.project(command.householdId(), command.projectId(), command.requesterId());
        var task = loader.task(project, command.taskId());
        announcer.forget(project, List.of(task));
        tasks.delete(task.id());
    }
}
