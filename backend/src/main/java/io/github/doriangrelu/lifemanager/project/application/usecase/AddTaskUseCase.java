package io.github.doriangrelu.lifemanager.project.application.usecase;

import io.github.doriangrelu.lifemanager.project.application.port.in.AddTask;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.AddTaskCommand;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.TaskView;
import io.github.doriangrelu.lifemanager.project.application.port.out.TaskRepository;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectInputs;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectAccess;
import io.github.doriangrelu.lifemanager.project.application.service.TaskAnnouncer;
import io.github.doriangrelu.lifemanager.project.domain.aggregate.ProjectTask;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Ajoute une tâche en fin de liste d'un projet. */
@Singleton
class AddTaskUseCase implements AddTask {

    private final ProjectAccess loader;
    private final TaskRepository tasks;
    private final ProjectInputs inputs;
    private final TaskAnnouncer announcer;

    AddTaskUseCase(ProjectAccess loader, TaskRepository tasks, ProjectInputs inputs, TaskAnnouncer announcer) {
        this.loader = loader;
        this.tasks = tasks;
        this.inputs = inputs;
        this.announcer = announcer;
    }

    @Override
    @Transactional(transactionManager = "project")
    public TaskView add(AddTaskCommand command) {
        var project = loader.project(command.householdId(), command.projectId(), command.requesterId());
        var task = ProjectTask.create(project, inputs.task(project.householdId(), command.input()),
                tasks.nextPosition(project.id()));
        tasks.save(task);
        announcer.announce(project, List.of(task));
        return TaskView.of(task);
    }
}
