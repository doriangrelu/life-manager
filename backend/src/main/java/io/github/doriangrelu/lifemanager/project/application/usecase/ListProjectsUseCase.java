package io.github.doriangrelu.lifemanager.project.application.usecase;

import io.github.doriangrelu.lifemanager.project.application.port.in.ListProjects;
import io.github.doriangrelu.lifemanager.project.application.port.in.query.ListProjectsQuery;
import io.github.doriangrelu.lifemanager.project.application.port.in.result.ProjectView;
import io.github.doriangrelu.lifemanager.project.application.port.out.ProjectRepository;
import io.github.doriangrelu.lifemanager.project.application.port.out.TaskRepository;
import io.github.doriangrelu.lifemanager.project.application.service.ProjectHouseholdAccess;
import io.github.doriangrelu.lifemanager.project.domain.aggregate.ProjectTask;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

/** Projets du foyer avec l'avancement de leurs tâches. */
@Singleton
class ListProjectsUseCase implements ListProjects {

    private final ProjectHouseholdAccess access;
    private final ProjectRepository projects;
    private final TaskRepository tasks;

    ListProjectsUseCase(ProjectHouseholdAccess access, ProjectRepository projects, TaskRepository tasks) {
        this.access = access;
        this.projects = projects;
        this.tasks = tasks;
    }

    @Override
    @Transactional(transactionManager = "project", readOnly = true)
    public List<ProjectView> list(ListProjectsQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        var byProject = tasks.findAllByHousehold(household).stream()
                .collect(Collectors.groupingBy(ProjectTask::projectId));
        return projects.findAllByHousehold(household).stream().map(project -> {
            List<ProjectTask> own = byProject.getOrDefault(project.id(), List.of());
            return ProjectView.of(project, done(own), own.size());
        }).toList();
    }

    static int done(List<ProjectTask> tasks) {
        return (int) tasks.stream().filter(ProjectTask::isDone).count();
    }
}
