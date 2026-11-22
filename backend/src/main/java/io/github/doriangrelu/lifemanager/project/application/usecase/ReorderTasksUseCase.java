package io.github.doriangrelu.lifemanager.project.application.usecase;

import io.github.doriangrelu.lifemanager.project.application.port.in.ReorderTasks;
import io.github.doriangrelu.lifemanager.project.application.port.in.command.ReorderTasksCommand;
import io.github.doriangrelu.lifemanager.project.application.port.out.TaskRepository;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectAccess;
import io.github.doriangrelu.lifemanager.project.domain.aggregate.ProjectTask;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Range les tâches d'un projet dans l'ordre donné (toutes ses tâches, chacune une fois). */
@Singleton
class ReorderTasksUseCase implements ReorderTasks {

    private final ProjectAccess loader;
    private final TaskRepository tasks;

    ReorderTasksUseCase(ProjectAccess loader, TaskRepository tasks) {
        this.loader = loader;
        this.tasks = tasks;
    }

    @Override
    @Transactional(transactionManager = "project")
    public void reorder(ReorderTasksCommand command) {
        var project = loader.project(command.householdId(), command.projectId(), command.requesterId());
        var byId = tasks.findAllByProject(project.id()).stream()
                .collect(Collectors.toMap(task -> task.id().value(), Function.<ProjectTask>identity()));
        var ids = command.taskIds();
        if (ids.size() != byId.size() || !new HashSet<UUID>(ids).equals(byId.keySet())) {
            throw new InvalidValueException("L'ordre doit citer chaque tâche du projet une fois");
        }
        for (int position = 0; position < ids.size(); position++) {
            var task = byId.get(ids.get(position));
            task.moveTo(position);
            tasks.save(task);
        }
    }
}
