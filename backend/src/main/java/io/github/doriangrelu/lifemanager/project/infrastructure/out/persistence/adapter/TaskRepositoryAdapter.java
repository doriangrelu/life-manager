package io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.project.application.port.out.TaskRepository;
import io.github.doriangrelu.lifemanager.project.domain.aggregate.ProjectTask;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.ProjectId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskDetails;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskId;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskState;
import io.github.doriangrelu.lifemanager.project.domain.valueobject.TaskTitle;
import io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.entity.ProjectTaskEntity;
import io.github.doriangrelu.lifemanager.project.infrastructure.out.persistence.repository.ProjectTaskJdbcRepository;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

/** Implémente le stockage des tâches avec PostgreSQL (table project_task). */
@Singleton
class TaskRepositoryAdapter implements TaskRepository {

    private final ProjectTaskJdbcRepository table;

    TaskRepositoryAdapter(ProjectTaskJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<ProjectTask> findById(TaskId id) {
        return table.findById(id.value()).map(TaskRepositoryAdapter::toDomain);
    }

    @Override
    public List<ProjectTask> findAllByProject(ProjectId projectId) {
        return table.findAllByProject(projectId.value()).stream().map(TaskRepositoryAdapter::toDomain).toList();
    }

    @Override
    public List<ProjectTask> findAllByHousehold(HouseholdId householdId) {
        return table.findAllByHousehold(householdId.value()).stream().map(TaskRepositoryAdapter::toDomain).toList();
    }

    @Override
    public int nextPosition(ProjectId projectId) {
        return table.nextPosition(projectId.value());
    }

    @Override
    public void save(ProjectTask task) {
        var entity = toEntity(task);
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }

    @Override
    public void delete(TaskId id) {
        table.deleteById(id.value());
    }

    @Override
    public void deleteAllByProject(ProjectId projectId) {
        table.deleteAllByProject(projectId.value());
    }

    private static ProjectTaskEntity toEntity(ProjectTask task) {
        var d = task.details();
        return new ProjectTaskEntity(task.id().value(), task.projectId().value(), task.householdId().value(),
                d.title().value(), d.assignee() == null ? null : d.assignee().value(), d.dueOn(), task.doneAt(),
                task.position());
    }

    private static ProjectTask toDomain(ProjectTaskEntity e) {
        var details = new TaskDetails(new TaskTitle(e.title()),
                e.assigneeId() == null ? null : new MemberId(e.assigneeId()), e.dueOn());
        return ProjectTask.restore(new TaskId(e.id()), new ProjectId(e.projectId()), new HouseholdId(e.householdId()),
                details, new TaskState(e.doneAt(), e.position()));
    }
}
