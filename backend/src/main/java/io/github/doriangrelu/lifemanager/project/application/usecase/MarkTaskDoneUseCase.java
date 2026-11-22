package io.github.doriangrelu.lifemanager.project.application.usecase;

import io.github.doriangrelu.lifemanager.project.application.port.in.MarkTaskDone;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.MarkTaskDoneCommand;
import io.github.doriangrelu.lifemanager.project.application.port.out.TaskRepository;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectAccess;
import io.github.doriangrelu.lifemanager.project.application.service.TaskAnnouncer;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.List;

/** Coche ou décoche une tâche ; une tâche faite n'est plus rappelée. */
@Singleton
class MarkTaskDoneUseCase implements MarkTaskDone {

    private final ProjectAccess loader;
    private final TaskRepository tasks;
    private final TaskAnnouncer announcer;
    private final Clock clock;

    MarkTaskDoneUseCase(ProjectAccess loader, TaskRepository tasks, TaskAnnouncer announcer, Clock clock) {
        this.loader = loader;
        this.tasks = tasks;
        this.announcer = announcer;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "project")
    public void mark(MarkTaskDoneCommand command) {
        var target = command.task();
        var project = loader.project(target.householdId(), target.projectId(), target.requesterId());
        var task = loader.task(project, target.taskId());
        task.markDone(command.done(), clock.instant());
        tasks.save(task);
        announcer.announce(project, List.of(task));
    }
}
