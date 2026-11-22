package io.github.doriangrelu.lifemanager.project.application.service;

import io.github.doriangrelu.lifemanager.project.application.port.out.ProjectRepository;
import io.github.doriangrelu.lifemanager.project.application.port.out.TaskRepository;
import io.github.doriangrelu.lifemanager.project.domain.aggregate.Project;
import io.github.doriangrelu.lifemanager.project.domain.aggregate.ProjectTask;
import io.github.doriangrelu.lifemanager.project.domain.exception.ProjectNotFoundException;
import io.github.doriangrelu.lifemanager.project.domain.exception.TaskNotFoundException;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskId;
import jakarta.inject.Singleton;

import java.util.UUID;

/** Code commun : retrouver un projet (ou une tâche) dans le foyer du demandeur. */
@Singleton
public class ProjectAccess {

    private final ProjectHouseholdAccess access;
    private final ProjectRepository projects;
    private final TaskRepository tasks;

    ProjectAccess(ProjectHouseholdAccess access, ProjectRepository projects, TaskRepository tasks) {
        this.access = access;
        this.projects = projects;
        this.tasks = tasks;
    }

    /** @throws ProjectNotFoundException si le projet n'existe pas dans ce foyer */
    public Project project(UUID householdId, UUID projectId, String requesterId) {
        var household = access.householdOfMember(householdId, requesterId);
        return projects.findById(new ProjectId(projectId))
                .filter(project -> project.belongsTo(household))
                .orElseThrow(ProjectNotFoundException::new);
    }

    /** @throws TaskNotFoundException si la tâche n'appartient pas à ce projet */
    public ProjectTask task(Project project, UUID taskId) {
        return tasks.findById(new TaskId(taskId))
                .filter(task -> task.belongsTo(project.id()))
                .orElseThrow(TaskNotFoundException::new);
    }
}
