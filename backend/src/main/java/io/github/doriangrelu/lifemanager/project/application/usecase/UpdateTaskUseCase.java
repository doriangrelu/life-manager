package io.github.doriangrelu.lifemanager.project.application.usecase;

import io.github.doriangrelu.lifemanager.project.application.port.in.UpdateTask;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.UpdateTaskCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.TaskView;
import io.github.doriangrelu.lifemanager.project.application.port.out.TaskRepository;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectInputs;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectAccess;
import io.github.doriangrelu.lifemanager.project.application.service.TaskAnnouncer;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Modifie le titre, le responsable ou la date d'une tâche. */
@Singleton
class UpdateTaskUseCase implements UpdateTask {

    private final ProjectAccess loader;
    private final TaskRepository tasks;
    private final ProjectInputs inputs;
    private final TaskAnnouncer announcer;

    UpdateTaskUseCase(ProjectAccess loader, TaskRepository tasks, ProjectInputs inputs, TaskAnnouncer announcer) {
        this.loader = loader;
        this.tasks = tasks;
        this.inputs = inputs;
        this.announcer = announcer;
    }

    @Override
    @Transactional(transactionManager = "project")
    public TaskView update(UpdateTaskCommand command) {
        var project = loader.project(command.householdId(), command.projectId(), command.requesterId());
        var task = loader.task(project, command.taskId());
        task.update(inputs.task(project.householdId(), command.input()));
        tasks.save(task);
        announcer.announce(project, List.of(task));
        return TaskView.of(task);
    }
}
